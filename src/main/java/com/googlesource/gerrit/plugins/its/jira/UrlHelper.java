
package com.googlesource.gerrit.plugins.its.jira;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlHelper {
  private static final Logger log = LoggerFactory.getLogger(UrlHelper.class);

  public static URL resolveUrl(URL url, String... paths) {
    if (url == null) {
      return url;
    }

    String relativePath = String.join("", Arrays.asList(paths));
    try {
      return new URL(url, relativePath);
    } catch (MalformedURLException e) {
      log.error("Unexpected exception while composing URL {} with path {}", url, relativePath, e);
      throw new IllegalArgumentException(e);
    }
  }

  public static URL adjustUrlPath(URL url) {
    if (url == null) {
      return url;
    }
    try {
      return url.getPath().endsWith("/") ? url : new URL(url, "/");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
