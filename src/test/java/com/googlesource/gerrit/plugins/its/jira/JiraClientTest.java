// Copyright (C) 2017 Android Open Source Project
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

package com.googlesource.gerrit.plugins.its.jira;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.googlesource.gerrit.plugins.its.base.its.InvalidTransitionException;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraIssue;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraProject;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraRestApi;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraRestApiProvider;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraServerInfo;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraTransition;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraTransition.Item;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraClientTest {
  private static final String COMMENT = "comment";
  private static final String COMMENT_BODY = format("{\"body\":\"%s\"}", COMMENT);
  private static final String JIRA_ISSUE = "1000";
  private static final String COMMENT_SPEC = "/" + JIRA_ISSUE + "/comment";
  private static final String TRANSITION = "transition";
  private static final String TRANSITION_BODY =
      format("{\"transition\":{\"name\":\"%s\",\"id\":\"1\"}}", TRANSITION);
  private static final String TRANSITION_SPEC = "/" + JIRA_ISSUE + "/transitions";

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Mock private JiraRestApiProvider restApiProvider;
  @Mock private JiraRestApi<JiraIssue> jiraIssue;
  @Mock private JiraRestApi<JiraProject[]> jiraProjects;
  @Mock private JiraRestApi<JiraServerInfo> jiraServerInfo;
  @Mock private JiraRestApi<JiraTransition> jiraTransition;

  private JiraClient jiraClient;

  @Before
  public void setUpJiraClient() {
    jiraClient = new JiraClient(restApiProvider);
  }

  @Test
  public void testIssueExists() throws IOException {
    when(restApiProvider.getIssue()).thenReturn(jiraIssue);
    when(jiraIssue.getResponseCode()).thenReturn(HTTP_OK);
    assertThat(jiraClient.issueExists(JIRA_ISSUE)).isTrue();
  }

  @Test
  public void testIssueNotFound() throws IOException {
    when(restApiProvider.getIssue()).thenReturn(jiraIssue);
    when(jiraIssue.getResponseCode()).thenReturn(HTTP_NOT_FOUND);
    assertThat(jiraClient.issueExists(JIRA_ISSUE)).isFalse();
  }

  @Test
  public void testIssueExistsForbidden() throws IOException {
    when(restApiProvider.getIssue()).thenReturn(jiraIssue);
    when(jiraIssue.getResponseCode()).thenReturn(HTTP_FORBIDDEN);
    assertThat(jiraClient.issueExists(JIRA_ISSUE)).isFalse();
  }

  @Test
  public void testIssueExistsUnexpectedError() throws IOException {
    when(restApiProvider.getIssue()).thenReturn(jiraIssue);
    when(jiraIssue.getResponseCode()).thenReturn(HTTP_INTERNAL_ERROR);
    thrown.expect(IOException.class);
    thrown.expectMessage("Unexpected HTTP code received:" + HTTP_INTERNAL_ERROR);
    jiraClient.issueExists(JIRA_ISSUE);
  }

  @Test
  public void testGetTransitions() throws IOException {
    Item[] items = new Item[3];
    JiraTransition transition = mock(JiraTransition.class);
    when(restApiProvider.get(JiraTransition.class, "/issue")).thenReturn(jiraTransition);
    when(jiraTransition.doGet("/" + JIRA_ISSUE + "/transitions", HTTP_OK)).thenReturn(transition);
    when(transition.getTransitions()).thenReturn(items);
    assertThat(jiraClient.getTransitions(JIRA_ISSUE).size()).isEqualTo(3);
  }

  @Test
  public void addCommentSucceeds() throws IOException {
    when(restApiProvider.getIssue()).thenReturn(jiraIssue);
    when(jiraIssue.getResponseCode()).thenReturn(HTTP_OK);
    jiraClient.addComment(JIRA_ISSUE, COMMENT);
    verify(jiraIssue).doPost(COMMENT_SPEC, COMMENT_BODY, HTTP_CREATED);
  }

  @Test
  public void addCommentFails() throws IOException {
    when(restApiProvider.getIssue()).thenReturn(jiraIssue);
    when(jiraIssue.getResponseCode()).thenReturn(HTTP_NOT_FOUND);
    jiraClient.addComment(JIRA_ISSUE, COMMENT);
    verify(jiraIssue, never()).doPost(COMMENT_SPEC, COMMENT_BODY, HTTP_CREATED);
  }

  @Test
  public void doTransitionSucceeds() throws Exception {
    JiraTransition.Item item = new JiraTransition.Item(TRANSITION, "1");
    Item[] items = {item};
    JiraTransition transition = mock(JiraTransition.class);
    when(restApiProvider.get(JiraTransition.class, "/issue")).thenReturn(jiraTransition);
    when(jiraTransition.doGet("/" + JIRA_ISSUE + "/transitions", HTTP_OK)).thenReturn(transition);
    when(transition.getTransitions()).thenReturn(items);
    when(restApiProvider.getIssue()).thenReturn(jiraIssue);
    when(jiraIssue.doPost(TRANSITION_SPEC, TRANSITION_BODY, HTTP_NO_CONTENT)).thenReturn(true);
    assertThat(jiraClient.doTransition(JIRA_ISSUE, TRANSITION)).isTrue();
  }

  @Test
  public void doTransitionFails() throws Exception {
    JiraTransition.Item item = new JiraTransition.Item("badTransition", "1");
    Item[] items = {item};
    JiraTransition transition = mock(JiraTransition.class);
    when(restApiProvider.get(JiraTransition.class, "/issue")).thenReturn(jiraTransition);
    when(jiraTransition.doGet("/" + JIRA_ISSUE + "/transitions", HTTP_OK)).thenReturn(transition);
    when(transition.getTransitions()).thenReturn(items);
    thrown.expect(InvalidTransitionException.class);
    thrown.expectMessage("Action " + TRANSITION + " not executable on issue " + JIRA_ISSUE);
    jiraClient.doTransition(JIRA_ISSUE, TRANSITION);
  }

  @Test
  public void testSysInfo() throws IOException {
    when(restApiProvider.getServerInfo()).thenReturn(jiraServerInfo);
    jiraClient.sysInfo();
    verify(jiraServerInfo).doGet("", HTTP_OK);
  }

  @Test
  public void testGetProjects() throws IOException {
    when(restApiProvider.getProjects()).thenReturn(jiraProjects);
    jiraClient.getProjects();
    verify(jiraProjects).doGet("", HTTP_OK);
  }

  @Test
  public void testHealthCheckAccess() throws IOException {
    JiraServerInfo info = mock(JiraServerInfo.class);
    when(jiraServerInfo.doGet("", HTTP_OK)).thenReturn(info);
    when(restApiProvider.getServerInfo()).thenReturn(jiraServerInfo);
    String expected = "{\"status\"=\"ok\"}";
    assertThat(jiraClient.healthCheckAccess()).isEqualTo(expected);
  }

  @Test
  public void testHealthCheckSysInfo() throws IOException {
    JiraServerInfo info = mock(JiraServerInfo.class);
    String jiraUrl = "http://jira_example.com";
    String jiraBuild = "b1";
    String jiraVersion = "v1";
    when(info.getBaseUri()).thenReturn(jiraUrl);
    when(info.getBuildNumber()).thenReturn(jiraBuild);
    when(info.getVersion()).thenReturn(jiraVersion);
    when(jiraServerInfo.doGet("", HTTP_OK)).thenReturn(info);
    when(restApiProvider.getServerInfo()).thenReturn(jiraServerInfo);
    String expected =
        format(
            "{\"status\"=\"ok\",\"system\"=\"Jira\",\"version\"=\"%s\",\"url\"=\"%s\",\"build\"=\"%s\"}",
            jiraVersion, jiraUrl, jiraBuild);
    assertThat(jiraClient.healthCheckSysinfo()).isEqualTo(expected);
  }
}
