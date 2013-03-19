package sh.app.sample_projects.it_embedded_http_server;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NetUtilsTest {
  private File downloadedFile;
  private File fileToBeDownloaded;

  @Before
  public void before() throws Exception {
    fileToBeDownloaded = getFileToBeDownloaded();
  }

  @After
  public void after() {
    if (!downloadedFile.delete())
      fail();
  }

  @Test
  public void downloadUrlFromHttp() throws Exception {
    EmbeddedHttpServer srv = null;

    try {
      String urlPath = "/testDownloadUrl/";

      srv = EmbeddedHttpServer.startHttpServer().withFileDownloadingHandler(urlPath, fileToBeDownloaded);

      downloadedFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "url-http.file");

      downloadedFile = NetUtils.downloadUrl(new URL(srv.getBaseUrl() + urlPath), downloadedFile);

      assertTrue(FileUtils.contentEquals(fileToBeDownloaded, downloadedFile));
    } finally {
      if (srv != null)
        srv.stop(1);
    }
  }

  @Test
  public void downloadUrlFromHttps() throws Exception {
    EmbeddedHttpServer srv = null;

    try {
      String urlPath = "/testDownloadUrl/";

      srv = EmbeddedHttpServer.startHttpsServer().withFileDownloadingHandler(urlPath, fileToBeDownloaded);

      downloadedFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "url-http.file");

      downloadedFile = NetUtils.downloadUrl(new URL(srv.getBaseUrl() + urlPath), downloadedFile);

      assertTrue(FileUtils.contentEquals(fileToBeDownloaded, downloadedFile));
    } finally {
      if (srv != null)
        srv.stop(1);
    }
  }

  @Test
  public void downloadUrlFromLocalFileSystem() throws Exception {
    downloadedFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "url-http.file");

    downloadedFile = NetUtils.downloadUrl(fileToBeDownloaded.toURI().toURL(), downloadedFile);

    assertTrue(FileUtils.contentEquals(fileToBeDownloaded, downloadedFile));
  }

  private File getFileToBeDownloaded() throws URISyntaxException {
    return new File(this.getClass().getResource("/download.me").toURI());
  }
}