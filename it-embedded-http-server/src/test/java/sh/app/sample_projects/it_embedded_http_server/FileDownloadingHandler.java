package sh.app.sample_projects.it_embedded_http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * The class represents a server handler triggered on incoming request.
 * <p>
 * The handler checks that a request is a HTTP GET and that url path is the expected one.
 * If all checks are passed it writes pre-configured file content to the HTTP response body.
 */
public class FileDownloadingHandler implements HttpHandler {
  /** URL path. */
  private final String urlPath;

  /** File to be downloaded. */
  private final File downloadFile;

  /**
   * Creates and configures FileDownloadingHandler.
   *
   * @param urlPath Url path on which a future GET request is going to be executed.
   * @param fileToBeDownloaded File to be written into the HTTP response.
   */
  public FileDownloadingHandler(String urlPath, File fileToBeDownloaded) {
    checkNotNull(urlPath);
    checkArgument(fileToBeDownloaded.exists());

    this.urlPath = urlPath;
    this.downloadFile = fileToBeDownloaded;
  }

  /**
   * Handles HTTP requests: checks that a request is a HTTP GET and that url path is the expected one.
   * If all checks are passed it writes pre-configured file content to the HTTP response body.
   *
   * @param exchange Wrapper above the HTTP request and response.
   */
  @Override public void handle(HttpExchange exchange) throws IOException {
    checkArgument("GET".equalsIgnoreCase(exchange.getRequestMethod()));

    // Check that a request has come to expected URL path.
    checkState(urlPath.equals(exchange.getRequestURI().toString()));

    exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
    exchange.sendResponseHeaders(200, 0);

    OutputStream resBody = exchange.getResponseBody();
    try {
      resBody.write(FileUtils.readFileToByteArray(downloadFile));
    }
    finally {
      resBody.close();
    }
  }
}

