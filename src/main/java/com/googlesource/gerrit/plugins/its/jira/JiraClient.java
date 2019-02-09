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

import static java.net.HttpURLConnection.*;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.its.InvalidTransitionException;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraComment;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraIssue;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraPageRequest;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraProject;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraRestApi;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraRestApiProvider;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraServerInfo;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraTransition;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraVersion;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraVersionsPage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public boolean issueExists(JiraItsServerInfo server, String issueKey) throws IOException {
    JiraRestApi<JiraIssue> api = apiBuilder.getIssue(server);
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
  public List<JiraTransition.Item> getTransitions(JiraItsServerInfo server, String issueKey)
      throws IOException {
    JiraRestApi<JiraTransition> api = apiBuilder.get(server, JiraTransition.class, "/issue");
    return Arrays.asList(api.doGet(issueKey + "/transitions", HTTP_OK).getTransitions());
  }

  /**
   * @param issueKey Jira Issue key
   * @param comment String to be added
   * @throws IOException
   */
  public void addComment(JiraItsServerInfo server, String issueKey, String comment)
      throws IOException {

    if (issueExists(server, issueKey)) {
      log.debug("Trying to add comment for issue {}", issueKey);
      apiBuilder
          .getIssue(server)
          .doPost(issueKey + "/comment", gson.toJson(new JiraComment(comment)), HTTP_CREATED);
      log.debug("Comment added to issue {}", issueKey);
    } else {
      log.error("Issue {} does not exist or no access permission", issueKey);
    }
  }

  public void createVersion(JiraItsServerInfo server, String projectKey, String version)
      throws IOException {
    log.debug("Trying to create version {} on project {}", version, projectKey);
    JiraVersion jiraVersion = JiraVersion.builder().project(projectKey).name(version).build();
    apiBuilder.getVersions(server).doPost("", gson.toJson(jiraVersion), HTTP_CREATED);
    log.debug("Version {} created on project {}", version, projectKey);
  }

  public void markVersionAsReleased(JiraItsServerInfo server, String projectKey, String version)
      throws IOException {
    JiraVersion jiraVersion = findVersion(server, projectKey, version);
    if (jiraVersion == null) {
      log.error(
          "Version {} of project {} does not exist or no access permission", version, projectKey);
      return;
    }

    log.debug(
        "Trying to mark version {} with id {} of project {} as released",
        version,
        jiraVersion.getId(),
        projectKey);

    JiraVersion markAsReleased =
        JiraVersion.builder().released(true).releaseDate(new Date()).build();
    apiBuilder.getVersions(server).doPut(jiraVersion.getId(), gson.toJson(markAsReleased), HTTP_OK);

    log.debug("Version {} of project {} was marked as released", version, projectKey);
  }

  private JiraVersion findVersion(JiraItsServerInfo server, String projectKey, String version)
      throws IOException {
    JiraRestApi<JiraVersionsPage> api = apiBuilder.getProjectVersions(server, projectKey);

    JiraPageRequest pageRequest = JiraPageRequest.builder().orderBy("-sequence").build();
    JiraVersion jiraVersion = null;
    while (pageRequest != null) {
      JiraVersionsPage versionsPage = api.doGet(pageRequest.toSpec(), HTTP_OK);
      jiraVersion = versionsPage.findByName(version);
      if (jiraVersion != null) {
        break;
      }
      pageRequest = versionsPage.nextPageRequest(pageRequest);
    }

    return jiraVersion;
  }

  /**
   * @param issueKey Jira Issue key
   * @param transition JiraTransition.Item to perform
   * @return true if successful
   */
  public boolean doTransition(JiraItsServerInfo server, String issueKey, String transition)
      throws IOException, InvalidTransitionException {
    log.debug("Making transition to {} for {}", transition, issueKey);
    JiraTransition.Item t = getTransitionByName(server, issueKey, transition);
    if (t == null) {
      throw new InvalidTransitionException(
          "Action " + transition + " not executable on issue " + issueKey);
    }
    log.debug("Transition issue {} to '{}' ({})", issueKey, transition, t.getId());
    return apiBuilder
        .getIssue(server)
        .doPost(issueKey + "/transitions", gson.toJson(new JiraTransition(t)), HTTP_NO_CONTENT);
  }

  /** @return Serverinformation of jira */
  public JiraServerInfo sysInfo(JiraItsServerInfo server) throws IOException {
    return apiBuilder.getServerInfo(server).doGet("", HTTP_OK);
  }

  /** @return List of all projects we have access to in jira */
  public JiraProject[] getProjects(JiraItsServerInfo server) throws IOException {
    return apiBuilder.getProjects(server).doGet("", HTTP_OK);
  }

  private JiraTransition.Item getTransitionByName(
      JiraItsServerInfo server, String issueKey, String transition) throws IOException {
    for (JiraTransition.Item t : getTransitions(server, issueKey)) {
      if (transition.equals(t.getName())) {
        return t;
      }
    }
    return null;
  }

  public String healthCheckAccess(JiraItsServerInfo server) throws IOException {
    sysInfo(server);
    String result = "{\"status\"=\"ok\"}";
    log.debug("Health check on access result: {}", result);
    return result;
  }

  public String healthCheckSysinfo(JiraItsServerInfo server) throws IOException {
    JiraServerInfo info = sysInfo(server);
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
