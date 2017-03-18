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
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  /**
   * @param issueKey Jira Issue key
   * @return true if issue exists
   */
  public boolean issueExists(String issueKey) throws JiraRestException {
    boolean ret = true;
    try {
      // This throws an exception if it does not exist: no exception means: issue exists
      doGet("/rest/api/2/issue/" + issueKey, null);
    } catch (JiraRestException e) {
      if (e.getStatusCode().get() == HttpURLConnection.HTTP_NOT_FOUND) {
        log.error("Issue " + issueKey + " not found ");
        ret = false;
      } else if (e.getStatusCode().get() == HttpURLConnection.HTTP_FORBIDDEN) {
        log.error("No permission to read Issue " + issueKey);
        ret = false;
      } else {
        throw e;
      }
    }
    return ret;
  }

  /**
   * @param issueKey Jira Issue key
   * @return Iterable of available transitions
   * @throws JiraRestException
   */
  public Iterable<JiraTransition.Item> getTransitions(String issueKey) throws JiraRestException {
    JiraTransition t = (JiraTransition) doGet("/rest/api/2/issue/" + issueKey + "/transitions",
      new TypeToken<JiraTransition>(){}.getType());
    return Arrays.asList(t.transitions);
  }

  /**
   * @param issueKey Jira Issue key
   * @param comment  String to be added
   * @throws JiraRestException
   */
  public void addComment(String issueKey, String comment) throws JiraRestException {
    if (!issueExists(issueKey)) {
       log.error("Issue {} does not exist or no access permission", issueKey);
       return;
    }
    log.debug("Trying to add comment for issue " + issueKey);
    doPost("/rest/api/2/issue/" + issueKey + "/comment", gson.toJson(new JiraComment(comment)));
    log.debug("Comment added to issue " + issueKey);
  }

  /**
   * @param issueKey   Jira Issue key
   * @param transition JiraTransition.Item to perform
   * @return true if successful
   */
  public boolean doTransition(String issueKey, String transition) throws JiraRestException, InvalidTransitionException {
    log.debug("Making transition to {} for {}", transition, issueKey);
    JiraTransition.Item t = getTransitionByName(getTransitions(issueKey), transition);
    if (t == null) {
      throw new InvalidTransitionException("Action " + transition
        + " not executable on issue " + issueKey);
    }
    log.debug("Transition issue {} to '{}' ({})", issueKey, transition, t.getId());
    doPost("/rest/api/2/issue/" + issueKey + "/transitions", gson.toJson(new JiraTransition(t)));
    return true;
  }

  /**
   * @return Serverinformation of jira
   */
  public JiraServerInfo sysInfo() throws JiraRestException {
    return (JiraServerInfo) doGet("/rest/api/2/serverInfo",
      new TypeToken<JiraServerInfo>(){}.getType());
  }

  /**
   * @return List of all projects we have access to in jira
   */
  public Iterable<JiraProject> getProjects() throws JiraRestException {
    return (List<JiraProject>) doGet("/rest/api/2/project",
      new TypeToken<List<JiraProject>>(){}.getType());
  }

  private JiraTransition.Item getTransitionByName(Iterable<JiraTransition.Item> transitions, String transition) {
    JiraTransition.Item ret = null;
    for (JiraTransition.Item t : transitions) {
      if (transition.equals(t.getName())) {
        ret = t;
        break;
      }
    }
    return ret;
  }

  /**
   * Do a simple GET request.
   */
  private Object doGet(String spec, Type typeOfObject) throws JiraRestException {
    try {
      URL url = new URL(baseUrl, spec);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("Authorization", "Basic " + auth);
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");

      if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new JiraRestException("Get Request failed: " + url.toString(),
          conn.getResponseCode());
      }

      Object object = null;
      if (typeOfObject != null) {
        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        object = gson.fromJson(isr, typeOfObject);
        isr.close();
      }
      conn.disconnect();
      return object;
    } catch (IOException e) {
      throw new JiraRestException(baseUrl + "/" + spec, e);
    }
  }

  /**
   * Do a simple POST request.
   * We do not care about what data the server returns, pass/fail is based
   * on HTTP-Response codes.
   */
  private void doPost(String spec, String jsonInput) throws JiraRestException {
    try {
      URL url = new URL(baseUrl, spec);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("Authorization", "Basic " + auth);
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");

      OutputStream os = conn.getOutputStream();
      os.write(jsonInput.getBytes());
      os.flush();

      if ((conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) &&
        (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT)) {
        throw new JiraRestException("Post Request failed: " + url.toString() + " ->" + jsonInput,
          conn.getResponseCode());
      }
      conn.disconnect();
    } catch (IOException e) {
      throw new JiraRestException(baseUrl + spec, e);
    }
  }
}
