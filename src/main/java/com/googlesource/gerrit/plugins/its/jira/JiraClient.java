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
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.its.InvalidTransitionException;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraComment;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraIssue;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraProject;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraRestApi;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraRestApiProvider;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraServerInfo;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraTransition;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.*;

public class JiraClient {
  private static final Logger log = LoggerFactory.getLogger(JiraClient.class);

  private final JiraRestApiProvider apiBuilder;
  private final Gson gson;

  @Inject
  public JiraClient(JiraRestApiProvider apiBuilder) {
    this.apiBuilder = apiBuilder;
    this.gson = new Gson();
  }

  /**
   * @param issueKey Jira Issue key
   * @return true if issue exists
   */
  public boolean issueExists(String issueKey) throws IOException {
    JiraRestApi<JiraIssue> api = apiBuilder.getIssue();

    api.doGet(issueKey, HTTP_OK, new int[] {HTTP_NOT_FOUND, HTTP_FORBIDDEN});
    Integer code = api.getResponseCode();
    switch (code) {
      case HTTP_OK:
        return true;
      case HTTP_NOT_FOUND:
        log.error("Issue {} not found", issueKey);
        return false;
      case HTTP_FORBIDDEN:
        log.error("No permission to read Issue {}", issueKey);
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
  public List<JiraTransition.Item> getTransitions(String issueKey) throws IOException {

    JiraRestApi<JiraTransition> api = apiBuilder.get(JiraTransition.class, "/issue");
    return Arrays.asList(api.doGet(issueKey + "/transitions", HTTP_OK).getTransitions());
  }

  /**
   * @param issueKey Jira Issue key
   * @param comment String to be added
   * @throws IOException
   */
  public void addComment(String issueKey, String comment) throws IOException {

    if (issueExists(issueKey)) {
      log.debug("Trying to add comment for issue {}", issueKey);
      apiBuilder
          .getIssue()
          .doPost(issueKey + "/comment", gson.toJson(new JiraComment(comment)), HTTP_CREATED);
      log.debug("Comment added to issue {}", issueKey);
    } else {
      log.error("Issue {} does not exist or no access permission", issueKey);
    }
  }

  public boolean projectExists(String projectKey) throws IOException {
    JiraProject[] projects = getProjects();
    return Stream.of(projects).map(JiraProject::getKey).anyMatch(projectKey::equals);
  }

  public void createVersion(String projectKey, String version) throws IOException {
    if (!projectExists(projectKey)) {
      log.error("Project {} does not exist or no access permission", projectKey);
      return;
    }

    log.debug("Trying to create version {} on project {}", version, projectKey);
    JiraVersion jiraVersion = JiraVersion.builder().project(projectKey).name(version).build();
    apiBuilder.getVersions().doPost("", gson.toJson(jiraVersion), HTTP_CREATED);
    log.debug("Version {} created on project {}", version, projectKey);
  }

  /**
   * @param issueKey Jira Issue key
   * @param transition JiraTransition.Item to perform
   * @return true if successful
   */
  public boolean doTransition(String issueKey, String transition)
      throws IOException, InvalidTransitionException {
    log.debug("Making transition to {} for {}", transition, issueKey);
    JiraTransition.Item t = getTransitionByName(issueKey, transition);
    if (t == null) {
      throw new InvalidTransitionException(
          "Action " + transition + " not executable on issue " + issueKey);
    }
    log.debug("Transition issue {} to '{}' ({})", issueKey, transition, t.getId());
    return apiBuilder
        .getIssue()
        .doPost(issueKey + "/transitions", gson.toJson(new JiraTransition(t)), HTTP_NO_CONTENT);
  }

  /** @return Serverinformation of jira */
  public JiraServerInfo sysInfo() throws IOException {
    return apiBuilder.getServerInfo().doGet("", HTTP_OK);
  }

  /** @return List of all projects we have access to in jira */
  public JiraProject[] getProjects() throws IOException {
    return apiBuilder.getProjects().doGet("", HTTP_OK);
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

  public String healthCheckAccess() throws IOException {
    sysInfo();
    String result = "{\"status\"=\"ok\"}";
    log.debug("Health check on access result: {}", result);
    return result;
  }

  public String healthCheckSysinfo() throws IOException {
    JiraServerInfo info = sysInfo();
    String result =
        "{\"status\"=\"ok\",\"system\"=\"Jira\",\"version\"=\""
            + info.getVersion()
            + "\",\"url\"=\""
            + info.getBaseUri()
            + "\",\"build\"=\""
            + info.getBuildNumber()
            + "\"}";
    log.debug("Health check on sysinfo result: {}", result);
    return result;
  }
}
