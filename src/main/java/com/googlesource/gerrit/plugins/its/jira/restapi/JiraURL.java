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

import org.eclipse.jgit.util.HttpSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class JiraURL {

  private static final Logger log = LoggerFactory.getLogger(JiraURL.class);

  private final URL url;

  public JiraURL(String spec) throws MalformedURLException {
    this.url = new URL(spec);
  }

  private JiraURL(URL url) {
    this.url = requireNonNull(url);
  }

  public JiraURL resolveUrl(String... paths) {
    String relativePath = String.join("", Arrays.asList(paths));
    try {
      return new JiraURL(new URL(url, relativePath));
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
    return new JiraURL(new URL(url, spec));
  }

  public HttpURLConnection openConnection(ProxySelector proxySelector) throws IOException {
    Proxy proxy = HttpSupport.proxyFor(proxySelector, url);
    return (HttpURLConnection) url.openConnection(proxy);
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
