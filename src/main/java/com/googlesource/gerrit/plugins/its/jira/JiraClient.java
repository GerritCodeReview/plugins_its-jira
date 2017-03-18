// Copyright (C) 2013 - 2017 The Android Open Source Project
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
import com.google.gson.reflect.TypeToken;
import com.googlesource.gerrit.plugins.its.base.its.InvalidTransitionException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.jgit.util.HttpSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;

/**
 * Jira Rest Client.
 */
public class JiraClient {
  private static final Logger log = LoggerFactory.getLogger(JiraClient.class);

  private final URL baseUrl;
  private final String auth;
  private final Gson gson;

  /**
   * @param url  jira url
   * @param user username of the jira user
   * @param pass password of the jira user
   * @throws MalformedURLException
   */
  public JiraClient(String url, String user, String pass) throws MalformedURLException {
    String auth = user + ":" + pass;
    this.auth = new String(Base64.encodeBase64(auth.getBytes()));
    this.baseUrl = new URL(url);
    this.gson = new Gson();
  }

  private class RestApiResult {
    int statusCode;
    Object data;

    RestApiResult(int statusCode, Object data) {
      this.statusCode = statusCode;
      this.data = data;
    }
  }

  /**
   * @param issueKey Jira Issue key
   * @return true if issue exists
   */
  public boolean issueExists(String issueKey) throws IOException {
    Integer code = doGet("/rest/api/2/issue/" + issueKey, null,
      new int[] {HTTP_OK, HTTP_NOT_FOUND, HTTP_FORBIDDEN}).statusCode;
    switch (code) {
    case HTTP_OK:
      return true;
    case HTTP_NOT_FOUND:
      log.error("Issue " + issueKey + " not found ");
      return false;
    case HTTP_FORBIDDEN:
      log.error("No permission to read Issue " + issueKey);
      return false;
    default:
      // Cannot happen due to passCodes filter
      throw new IOException("Unexpected HTTP code received:" + code.toString());
    }
  }

  /**
   * @param issueKey Jira Issue key
   * @return Iterable of available transitions
   * @throws IOException
   */
  public Iterable<JiraTransition.Item> getTransitions(String issueKey)
    throws IOException {
    RestApiResult res = doGet("/rest/api/2/issue/" + issueKey + "/transitions",
      new TypeToken<JiraTransition>(){}.getType(), new int[] {HTTP_OK});
    return Arrays.asList(((JiraTransition) res.data).transitions);
  }

  /**
   * @param issueKey Jira Issue key
   * @param comment  String to be added
   * @throws IOException
   */
  public void addComment(String issueKey, String comment) throws IOException {
    if (issueExists(issueKey)) {
      log.debug("Trying to add comment for issue {}", issueKey);
      doPost("/rest/api/2/issue/" + issueKey + "/comment", null,
        gson.toJson(new JiraComment(comment)), new int[] {HTTP_CREATED});
      log.debug("Comment added to issue {}", issueKey);
    } else {
       log.error("Issue {} does not exist or no access permission", issueKey);
    }
  }

  /**
   * @param issueKey   Jira Issue key
   * @param transition JiraTransition.Item to perform
   * @return true if successful
   */
  public boolean doTransition(String issueKey, String transition)
    throws IOException, InvalidTransitionException {
    log.debug("Making transition to {} for {}", transition, issueKey);
    JiraTransition.Item t = getTransitionByName(issueKey, transition);
    if (t == null) {
      throw new InvalidTransitionException("Action " + transition
        + " not executable on issue " + issueKey);
    }
    log.debug("Transition issue {} to '{}' ({})", issueKey, transition, t.getId());
    doPost("/rest/api/2/issue/" + issueKey + "/transitions", null,
      gson.toJson(new JiraTransition(t)), new int[] {HTTP_NO_CONTENT});
    return true;
  }

  /**
   * @return Serverinformation of jira
   */
  public JiraServerInfo sysInfo() throws IOException {
    return (JiraServerInfo) doGet("/rest/api/2/serverInfo",
      new TypeToken<JiraServerInfo>(){}.getType(), new int[] {HTTP_OK}).data;
  }

  /**
   * @return List of all projects we have access to in jira
   */
  public Iterable<JiraProject> getProjects() throws IOException {
    return (List<JiraProject>) doGet("/rest/api/2/project",
      new TypeToken<List<JiraProject>>(){}.getType(), new int[] {HTTP_OK}).data;
  }

  private JiraTransition.Item getTransitionByName(String issueKey, String transition)
    throws IOException {
    for (JiraTransition.Item t : getTransitions(issueKey)) {
      if (transition.equals(t.getName())) {
        return t;
      }
    }
    return null;
  }

  /**
   * Do a simple GET request.
   * Object of type 'typeOfObject' is returned containing the parsed JSON data
   * null is returned if typeOfObject is null. To be used when there is no
   * interest in the actual content to be retrieved by the GET request, but
   * if the only interest is the existence and accessibility of the endpoint.
   */
  private RestApiResult doGet(String spec, Type typeOfObject, int[] passCodes)
      throws IOException {
    HttpURLConnection conn = null;
    try {
      conn = prepHttpConnection(spec, false);
      int code = validateResponse(conn, passCodes);
      Object data = readIncomingData(typeOfObject, conn);
      return new RestApiResult(code, data);
    } finally {
      if (conn!= null) {
        conn.disconnect();
      }
    }
  }

  /**
   * Do a simple POST request.
   * Object of type 'typeOfObject' is returned containing the parsed JSON data
   * null is returned if typeOfObject is null.
   */
  private RestApiResult doPost(String spec, Type typeOfObject,
      String jsonInput, int[] passCodes) throws IOException {
    HttpURLConnection conn = null;
    try {
      conn = prepHttpConnection(spec, true);
      writePostData(jsonInput, conn);
      int code = validateResponse(conn, passCodes);
      Object data = readIncomingData(typeOfObject, conn);
      return new RestApiResult(code, data);
    } finally {
      if (conn!= null) {
        conn.disconnect();
      }
    }
  }

  private HttpURLConnection prepHttpConnection(String spec, boolean isPostRequest)
    throws IOException {
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
  private Object readIncomingData(Type typeOfObject, HttpURLConnection conn)
    throws IOException {
    if (typeOfObject != null) {
      try (InputStreamReader isr = new InputStreamReader(conn.getInputStream())) {
        return gson.fromJson(isr, typeOfObject);
      }
    }
    return null;
  }

  /**
   * Checks if the connection returned one of the provides passCodes.
   * If not, an JiraException exception is thrown.
   * If it was part of the list, then the actual response code is returned.
   */
  private int validateResponse(HttpURLConnection conn, int[] passCodes)
    throws IOException {
    int code = conn.getResponseCode();
    if (!ArrayUtils.contains(passCodes, code)) {
      throw new IOException("Request failed");
    }
    return code;
  }
}
