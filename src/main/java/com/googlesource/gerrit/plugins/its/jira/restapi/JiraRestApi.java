// Copyright (C) 2017 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
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

package com.googlesource.gerrit.plugins.its.jira;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.jgit.util.HttpSupport;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;

/**
 * Jira Rest Client.
 */
public class JiraRestApi<T> {

  private final URL baseUrl;
  private final String auth;
  private final Gson gson;

  private int responseCode;
  private T data;

  /**
   * @param url  jira url
   * @param user username of the jira user
   * @param pass password of the jira user
   * @throws MalformedURLException
   */
  public JiraRestApi(String url, String user, String pass) throws MalformedURLException {
    String auth = user + ":" + pass;
    this.auth = new String(Base64.encodeBase64(auth.getBytes()));
    this.baseUrl = new URL(url);
    this.gson = new Gson();
  }

  public int getResponseCode() {
    return responseCode;
  }

  /**
   * Do a simple GET request.
   * Object of type 'T' is returned containing the parsed JSON data
   */
  public T doGet(String spec, int[] passCodes, Class<T> classOfT) throws IOException {
    HttpURLConnection conn = null;
    try {
      conn = prepHttpConnection(spec, false);
      validateResponse(conn, passCodes);
      readIncomingData(conn, classOfT);
      return data;
    } finally {
      if (conn!= null) {
        conn.disconnect();
      }
    }
  }

  /**
   * Do a simple POST request.
   * Object of type 'T' is returned containing the parsed JSON data
   */
  public T doPost(String spec, String jsonInput, int[] passCodes, Class<T> classOfT) throws IOException {
    HttpURLConnection conn = null;
    try {
      conn = prepHttpConnection(spec, true);
      writePostData(jsonInput, conn);
      validateResponse(conn, passCodes);
      readIncomingData(conn, classOfT);
      return data;
    } finally {
      if (conn!= null) {
        conn.disconnect();
      }
    }
  }

  private HttpURLConnection prepHttpConnection(String spec, boolean isPostRequest) throws IOException {
    URL url = new URL(baseUrl, spec);
    ProxySelector proxySelector = ProxySelector.getDefault();
    Proxy proxy = HttpSupport.proxyFor(proxySelector, url);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
    conn.setRequestProperty("Authorization", "Basic " + auth);
    conn.setRequestProperty("Content-Type", "application/json");
    if (isPostRequest) {
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
    } else {
      conn.setRequestMethod("GET");
    }
    return conn;
  }

  /**
   * Write the Read the returned data from the HTTP connection.
   */
  private void writePostData(String postData, HttpURLConnection conn) throws IOException {
    if (postData != null) {
      try (OutputStream os = conn.getOutputStream()) {
        os.write(postData.getBytes());
        os.flush();
      }
    }
  }

  /**
   * Read the returned data from the HTTP connection.
   */
  private void readIncomingData(HttpURLConnection conn, Class<T> classOfT) throws IOException {
    try (InputStreamReader isr = new InputStreamReader(conn.getInputStream())) {
      data = (T) gson.fromJson(isr, classOfT);
    }
  }

  /**
   * Checks if the connection returned one of the provides passCodes.
   * If not, an IOException exception is thrown.
   * If it was part of the list, then the actual response code is returned.
   */
  private void validateResponse(HttpURLConnection conn, int[] passCodes) throws IOException {
    responseCode = conn.getResponseCode();
    if (!ArrayUtils.contains(passCodes, responseCode)) {
      throw new IOException("Request failed");
    }
  }
}
