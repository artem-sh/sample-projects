package sh.app.sample_projects.it_embedded_http_server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.KeyStore;

/**
 * Embedded HTTP/HTTPS server implementation aimed to simplify tests development
 * that need to make HTTP(s) interactions.
 * <p>
 * NOTE: this implementation is NOT thread-safe.
 */
public class EmbeddedHttpServer {
  /** SSL version used for HTTPS support. */
  private static final String SSL_VERSION = "SSLv3";

  /** Default hostname to bind the server to. */
  private static final String HOSTNAME_TO_BIND_SRV = "localhost";

  /** Simple Oracle HTTP server used as main workhorse. */
  private HttpServer httpSrv;

  /** Store exact protocol (HTTP or HTTPS) which we are running at. */
  private String proto;

  /** Keystore filename. */
  private static final String KEYSTORE_FILENAME = "sample-keystore.jks";

  /** Keystore password. */
  private static final char[] KEYSTORE_PASSWORD = "secret".toCharArray();

  /**
   * Private constructor to promote server creation and initialization in <i>Builder pattern</i> style.
   */
  private EmbeddedHttpServer() {}


  /**
   * Creates and starts embedded HTTP server.
   *
   * @return Started HTTP server instance.
   */
  public static EmbeddedHttpServer startHttpServer() throws Exception {
    return createAndStart(false);
  }

  /**
   * Creates and starts embedded HTTPS server.
   *
   * @return Started HTTPS server instance.
   */
  public static EmbeddedHttpServer startHttpsServer() throws Exception {
    return createAndStart(true);
  }

  /**
   * Configures server with test-specific HTTP handler.
   *
   * @return Configured HTTP(s) server.
   */
  public EmbeddedHttpServer withHandler(HttpHandler handler) {
    httpSrv.createContext("/", handler);

    return this;
  }

  /**
   * Stops server by closing the listening socket and disallowing any new exchanges
   * from being processed.
   *
   * @param delay Maximum time in seconds to wait until exchanges have finished.
   */
  public void stop(int delay) {
    httpSrv.stop(delay);
  }

  /**
   * Returns base server url in the form <i>protocol://serverHostName:serverPort</i>.
   *
   * @return Base server url.
   */
  public String getBaseUrl() {
    return proto + "://" + httpSrv.getAddress().getHostName() + ":" + httpSrv.getAddress().getPort();
  }

  /**
   * Internal method which creates and starts the server.
   *
   * @param httpsMode True if the server to be started is HTTPS, false otherwise.
   * @return Started server.
   */
  private static EmbeddedHttpServer createAndStart(boolean httpsMode) throws Exception {
    HttpServer httpSrv;
    InetSocketAddress addrToBind = new InetSocketAddress(HOSTNAME_TO_BIND_SRV, getAvailablePort());

    if (httpsMode) {
      HttpsServer httpsSrv = HttpsServer.create(addrToBind, 0);

      httpsSrv.setHttpsConfigurator(new HttpsConfigurator(getSslContext()));

      httpSrv = httpsSrv;
    }
    else {
      httpSrv = HttpServer.create(addrToBind, 0);
    }

    EmbeddedHttpServer embeddedHttpSrv = new EmbeddedHttpServer();

    embeddedHttpSrv.proto = httpsMode ? "https" : "http";
    embeddedHttpSrv.httpSrv = httpSrv;
    embeddedHttpSrv.httpSrv.start();

    return embeddedHttpSrv;
  }

  /**
   * Return configured SSL context. Context is created for the given keystore and the given SSL version.
   *
   * @return Configured SSL context.
   */
  private static SSLContext getSslContext() throws Exception {
    KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());

    store.load(new FileInputStream(new File(EmbeddedHttpServer.class.getResource("/" +  KEYSTORE_FILENAME).toURI())),
        KEYSTORE_PASSWORD);

    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

    kmf.init(store, KEYSTORE_PASSWORD);

    SSLContext sslCtx = SSLContext.getInstance(SSL_VERSION);

    sslCtx.init(kmf.getKeyManagers(), null, null);

    return sslCtx;
  }


  /**
   * Returns a port number which was available for the moment of the method call.
   *
   * @return Available port number.
   */
  private static int getAvailablePort() throws IOException {
    int httpSrvPort;

    ServerSocket s = new ServerSocket(0);

    try {
      httpSrvPort = s.getLocalPort();
    }
    finally {
      s.close();
    }

    return httpSrvPort;
  }
}
