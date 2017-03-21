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

package com.googlesource.gerrit.plugins.its.jira.restapi;

import com.google.gson.Gson;

import java.util.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jgit.util.HttpSupport;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;

/**
 * Jira Rest Client.
 */
public class JiraRestApi<T> {
  private static final String BASE_PREFIX = "/rest/api/2";
  private final URL baseUrl;
  private final String auth;
  private final Gson gson;

  private final Class<T> classOfT;
  private final String classPrefix;
  private T data;
  private int responseCode;

  /**
   * @param url jira url
   * @param user username of the jira user
   * @param pass password of the jira user
   */
  JiraRestApi(URL url, String user, String pass, Class<T> classOfT,
      String classPrefix) {
    String auth = user + ":" + pass;
    this.auth = new String(Base64.getEncoder().encodeToString(auth.getBytes()));
    this.baseUrl = url;
    this.gson = new Gson();
    this.classOfT = classOfT;
    this.classPrefix = classPrefix;
  }

  public int getResponseCode() {
    return responseCode;
  }

  /**
   * Do a simple GET request. Object of type 'T' is returned containing the
   * parsed JSON data
   *
   * @param passCode HTTP response code required to mark this GET as success
   * @param failCodes HTTP response codes allowed and not fail on unexcepted
   *        response
   * @throws IOException generated if unexpected failCode is returned
   */
  public T doGet(String spec, int passCode, int[] failCodes)
      throws IOException {
    HttpURLConnection conn = prepHttpConnection(spec, false);
    try {
      if (validateResponse(conn, passCode, failCodes)) {
        readIncomingData(conn);
      }
      return data;
    } finally {
      conn.disconnect();
    }
  }

  public T doGet(String spec, int passCode) throws IOException {
    return doGet(spec, passCode, null);
  }

  /**
   * Do a simple POST request.
   */
  public boolean doPost(String spec, String jsonInput, int passCode)
      throws IOException {
    HttpURLConnection conn = prepHttpConnection(spec, true);
    try {
      writePostData(jsonInput, conn);
      return validateResponse(conn, passCode, null);
    } finally {
      conn.disconnect();
    }
  }

  private HttpURLConnection prepHttpConnection(String spec,
      boolean isPostRequest) throws IOException {
    URL url = new URL(baseUrl, BASE_PREFIX + classPrefix + spec);
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
  private void writePostData(String postData, HttpURLConnection conn)
      throws IOException {
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
  private void readIncomingData(HttpURLConnection conn) throws IOException {
    try (InputStreamReader isr = new InputStreamReader(conn.getInputStream())) {
      data = gson.fromJson(isr, classOfT);
    }
  }

  /**
   * Checks if the connection returned one of the provides pass or fail Codes.
   * If not, an IOException exception is thrown. If it was part of the list,
   * then the actual response code is returned. returns true if valid response
   * is returned, otherwise false
   */
  private boolean validateResponse(HttpURLConnection conn, int passCode,
      int[] failCodes) throws IOException {
    responseCode = conn.getResponseCode();
    if (responseCode == passCode) {
      return true;
    }
    if ((failCodes == null)
        || (!ArrayUtils.contains(failCodes, responseCode))) {
      throw new IOException("Request failed");
    }
    return false;
  }
}
