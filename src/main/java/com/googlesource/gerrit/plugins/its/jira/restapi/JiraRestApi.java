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
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.Gson;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProxySelector;
import java.util.Base64;
import org.apache.commons.lang3.ArrayUtils;

/** 
 * Jira Rest Client.
 * Note: The current implementation supports both Basic Authentication (username/password) 
 * and Personal Access Token (PAT) authentication.
 * When user is null or "_", Bearer authentication (PAT) will be used.
 * In all other cases, Basic authentication will be used.
 */
public class JiraRestApi<T> {

  private static final String BASE_PREFIX = "rest/api/2";

  private final JiraURL baseUrl;
  private final String auth;
  private final boolean useBearerAuth;
  private final Gson gson;

  private final Class<T> classOfT;
  private T data;
  private int responseCode;

  /**
   * Create a new Jira REST API client
   *
   * @param url jira url
   * @param user username of the jira user
   * @param pass password of the jira user
   * @param classOfT class type of the object requested
   * @param classPrefix prefix for the rest api request
   * @note When user is null or "_", pass will be treated as Personal Access Token
   */
  @Inject
  public JiraRestApi(JiraURL url, String user, String pass, Class<T> classOfT, String classPrefix) {
    this.useBearerAuth = user == null || "_".equals(user);
    this.auth = useBearerAuth ? pass : encode(user, pass);
    this.baseUrl = url == null ? url : url.resolveUrl(BASE_PREFIX, classPrefix, "/");
    this.gson = new Gson();
    this.classOfT = classOfT;
  }

  private static String encode(String user, String pass) {
    return Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
  }

  public int getResponseCode() {
    return responseCode;
  }

  /**
   * Do a simple GET request. Object of type 'T' is returned containing the parsed JSON data
   *
   * @param passCode HTTP response code required to mark this GET as success
   * @param failCodes HTTP response codes allowed and not fail on unexpected response
   * @throws IOException generated if unexpected failCode is returned
   */
  public T doGet(String spec, int passCode, int[] failCodes) throws IOException {
    HttpURLConnection conn = openConnection(spec, "GET", false);
    try {
      if (validateResponse(conn, new int[] {passCode}, failCodes)) {
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

  JiraURL getBaseUrl() {
    return baseUrl;
  }

  /** Do a simple POST request. */
  public boolean doPost(String spec, String jsonInput, int passCode) throws IOException {
    return sendPayload("POST", spec, jsonInput, passCode);
  }

  /** Do a simple PUT request. */
  public boolean doPut(String spec, String jsonInput, int passCode) throws IOException {
    return sendPayload("PUT", spec, jsonInput, passCode);
  }

  /** Open an HTTP connection with the Jira's endpoint URL. */
  public HttpURLConnection openConnection(String spec, String method, boolean withPayload)
      throws IOException {
    JiraURL url = baseUrl.withSpec(spec);
    ProxySelector proxySelector = ProxySelector.getDefault();
    HttpURLConnection conn = url.openConnection(proxySelector);

    if (useBearerAuth) {
      conn.setRequestProperty("Authorization", "Bearer " + auth);
    } else {
      conn.setRequestProperty("Authorization", "Basic " + auth);
    }
    conn.setRequestProperty("Content-Type", "application/json");

    conn.setRequestMethod(method.toUpperCase());
    if (withPayload) {
      conn.setDoOutput(true);
    }
    return conn;
  }

  private boolean sendPayload(String method, String spec, String jsonInput, int passCode)
      throws IOException {
    return sendPayload(method, spec, jsonInput, new int[] {passCode});
  }

  public boolean sendPayload(String method, String spec, String jsonInput, int[] passCodes)
      throws IOException {
    HttpURLConnection conn = openConnection(spec, method, true);
    try {
      writeBodyData(jsonInput, conn);
      return validateResponse(conn, passCodes, null);
    } finally {
      conn.disconnect();
    }
  }

  /** Write the data to the HTTP connection. */
  private void writeBodyData(String data, HttpURLConnection conn) throws IOException {
    if (data != null) {
      try (OutputStream os = conn.getOutputStream()) {
        os.write(data.getBytes(UTF_8));
        os.flush();
      }
    }
  }

  /** Read the returned data from the HTTP connection. */
  private void readIncomingData(HttpURLConnection conn) throws IOException {
    try (InputStreamReader isr = new InputStreamReader(conn.getInputStream())) {
      data = gson.fromJson(isr, classOfT);
    }
  }

  /**
   * Checks if the connection returned one of the provides pass or fail Codes. If not, an
   * IOException exception is thrown. If it was part of the list, then the actual response code is
   * returned. returns true if valid response is returned, otherwise false
   */
  private boolean validateResponse(HttpURLConnection conn, int[] passCodes, int[] failCodes)
      throws IOException {
    responseCode = conn.getResponseCode();
    if (ArrayUtils.contains(passCodes, responseCode)) {
      return true;
    }
    if ((failCodes == null) || (!ArrayUtils.contains(failCodes, responseCode))) {
      throw new IOException(
          "Request failed: "
              + conn.getURL()
              + " - "
              + conn.getResponseCode()
              + " - "
              + conn.getResponseMessage());
    }
    return false;
  }
}
