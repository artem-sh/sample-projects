package sh.app.sample_projects.it_embedded_http_server;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import static com.google.common.base.Preconditions.checkNotNull;

public class NetUtils {
  /** Default buffer size = 4K. */
  private static final int BUF_SIZE = 4096;

  /** Secure socket protocol to use. */
  private static final String HTTPS_PROTOCOL = "TLS";

  /**
   * Downloads resource by URL into file.
   *
   * @param url  URL to download.
   * @param file File where downloaded resource should be stored.
   * @return File where downloaded resource should be stored.
   * @throws IOException If error occurred.
   */
  public static File downloadUrl(URL url, File file) throws IOException {
    checkNotNull(url);
    checkNotNull(file);

    InputStream in = null;
    OutputStream out = null;

    try {
      URLConnection conn = url.openConnection();

      if (conn instanceof HttpsURLConnection) {
        HttpsURLConnection https = (HttpsURLConnection) conn;

        https.setHostnameVerifier(new AlwaysPositiveHostnameVerifier());

        SSLContext ctx = SSLContext.getInstance(HTTPS_PROTOCOL);

        ctx.init(null, getTrustManagers(), null);

        // Initialize socket factory.
        https.setSSLSocketFactory(ctx.getSocketFactory());
      }

      in = conn.getInputStream();

      if (in == null) {
        throw new IOException("Failed to open connection: " + url.toString());
      }

      out = new BufferedOutputStream(new FileOutputStream(file));

      copy(in, out);
    } catch (NoSuchAlgorithmException e) {
      throw new IOException("Failed to open HTTPs connection [url=" + url.toString() + ", msg=" + e + ']', e);
    } catch (KeyManagementException e) {
      throw new IOException("Failed to open HTTPs connection [url=" + url.toString() + ", msg=" + e + ']', e);
    } finally {
      close(in);
      close(out);
    }

    return file;
  }

  /**
   * Construct array with one trust manager which don't reject input certificates.
   *
   * @return Array with one X509TrustManager implementation of trust manager.
   */
  private static TrustManager[] getTrustManagers() {
    return new TrustManager[]{
        new X509TrustManager() {
          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return null;
          }

          @Override
          public void checkClientTrusted(X509Certificate[] certs, String authType) {
          }

          @Override
          public void checkServerTrusted(X509Certificate[] certs, String authType) {
          }
        }
    };
  }

  /**
   * Copies input byte stream to output byte stream.
   */
  private static int copy(InputStream in, OutputStream out) throws IOException {
    checkNotNull(in);
    checkNotNull(out);

    byte[] buf = new byte[BUF_SIZE];

    int cnt = 0;

    for (int n; (n = in.read(buf)) > 0; ) {
      out.write(buf, 0, n);

      cnt += n;
    }

    return cnt;
  }

  /**
   * Closes given resource logging possible checked exception.
   */
  private static void close(Closeable rsrc) {
    try {
      rsrc.close();
    } catch (IOException e) {
      System.out.println("Failed to close resource: " + e.getMessage());
    }
  }


  /**
   * Verifier always returns successful result for any host.
   */
  private static class AlwaysPositiveHostnameVerifier implements HostnameVerifier {
    // Remote host trusted by default.
    @Override
    public boolean verify(String hostname, SSLSession ses) {
      return true;
    }
  }
}