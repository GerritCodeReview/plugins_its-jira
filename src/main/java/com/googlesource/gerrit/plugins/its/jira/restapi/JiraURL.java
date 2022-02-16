// Copyright (C) 2018 Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License"),
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.its.jira.restapi;

import static java.util.Objects.requireNonNull;

import com.google.common.base.CharMatcher;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import org.eclipse.jgit.util.HttpSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraURL {
  /** Suffix to create a comment link based on this URL */
  public static final String URL_SUFFIX = "/browse/$1";

  private static final Logger log = LoggerFactory.getLogger(JiraURL.class);

  private final URL url;

  private final int connectionTimeoutMs;

  private final int readTimeoutMs;

  public static URL validateUrl(String spec) throws MalformedURLException {
    return new URL(CharMatcher.is('/').trimFrom(spec) + "/");
  }

  public JiraURL(String spec, Duration connectionTimeout, Duration readTimeout)
      throws MalformedURLException {
    this.url = validateUrl(spec);
    this.connectionTimeoutMs = (int) connectionTimeout.toMillis();
    this.readTimeoutMs = (int) readTimeout.toMillis();
  }

  private JiraURL(URL url, int connectionTimeoutMs, int readTimeoutMs) {
    this.url = requireNonNull(url);
    this.connectionTimeoutMs = connectionTimeoutMs;
    this.readTimeoutMs = readTimeoutMs;
  }

  public JiraURL resolveUrl(String... paths) {
    String relativePath = String.join("", paths);
    try {
      return new JiraURL(new URL(url, relativePath), connectionTimeoutMs, readTimeoutMs);
    } catch (MalformedURLException e) {
      log.error("Unexpected exception while composing URL {} with path {}", url, relativePath, e);
      throw new IllegalArgumentException(e);
    }
  }

  public JiraURL adjustUrlPath() {
    try {
      return url.getPath().endsWith("/") ? this : this.withSpec("/");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public JiraURL withSpec(String spec) throws MalformedURLException {
    return new JiraURL(new URL(url, spec), connectionTimeoutMs, readTimeoutMs);
  }

  public HttpURLConnection openConnection(ProxySelector proxySelector) throws IOException {
    Proxy proxy = HttpSupport.proxyFor(proxySelector, url);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
    conn.setConnectTimeout(connectionTimeoutMs);
    conn.setReadTimeout(readTimeoutMs);
    return conn;
  }

  public String getPath() {
    return url.getPath();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JiraURL jiraURL = (JiraURL) o;
    return Objects.equals(url, jiraURL.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }

  @Override
  public String toString() {
    return url.toString();
  }
}
