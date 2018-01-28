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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.google.common.truth.Truth.assertThat;
import static com.googlesource.gerrit.plugins.its.jira.restapi.JiraRestApi.BASE_PREFIX;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.googlesource.gerrit.plugins.its.jira.JiraConfig;
import java.io.IOException;
import java.net.URL;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class JiraRestApiTest {
  private static final String COMMENT_CLASS_PREFIX = "/comment/";
  private static final String COMMENT_REQUEST_BODY = "{\"body\":\"test_comment\"}";
  private static final String ISSUE_CLASS_PREFIX = "/issue/";
  private static final String ISSUE_RESPONSE_BODY = "{\"issue\":{\"id\":\"1\", \"key\":\"test\"}}";
  private static final String JIRA_ISSUE = "JIRA-1000";

  @Rule public WireMockRule wireMockRule = new WireMockRule(0);
  @Rule public ExpectedException thrown = ExpectedException.none();

  private URL url;

  @Mock private JiraConfig jiraConfig;

  private JiraRestApi restApi;

  public void setUpCommonMocks() {
    when(jiraConfig.getJiraUrl()).thenReturn(url);
    when(jiraConfig.getUsername()).thenReturn("user");
    when(jiraConfig.getPassword()).thenReturn("pass");
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrl() throws Exception {
    String nonRootJiraUrl = "http://localhost:" + wireMockRule.port();
    url = new URL(nonRootJiraUrl);
    setUpCommonMocks();
    wireMockRule.givenThat(
        get(urlEqualTo("/myroot/" + BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_OK)));
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(nonRootJiraUrl);
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrlNotEndingWithSlash() throws Exception {
    String nonRootJiraUrl = "http://localhost:" + wireMockRule.port();
    url = new URL(nonRootJiraUrl);
    setUpCommonMocks();
    wireMockRule.givenThat(
        get(urlEqualTo("/myroot" + BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_OK)));
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(nonRootJiraUrl);
  }

  @Test
  public void testJiraServerInfoForRootJiraUrl() throws Exception {
    String rootJiraUrl = "http://localhost:" + wireMockRule.port();
    url = new URL(rootJiraUrl);
    setUpCommonMocks();
    wireMockRule.givenThat(
        get(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_OK)));
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(rootJiraUrl);
  }

  @Test
  public void testGetResponse() throws Exception {
    url = new URL("http://localhost:" + wireMockRule.port());
    setUpCommonMocks();
    wireMockRule.givenThat(
        get(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_OK)));
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    restApi.doGet(JIRA_ISSUE, HTTP_OK);
    assertThat(restApi.getResponseCode()).isEqualTo(HTTP_OK);
  }

  @Test
  public void doGetReturnsRightObject() throws IOException {
    url = new URL("http://localhost:" + wireMockRule.port());
    setUpCommonMocks();
    wireMockRule.givenThat(
        get(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_OK).withBody(ISSUE_RESPONSE_BODY)));
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    assertThat(restApi.doGet(JIRA_ISSUE, HTTP_OK)).isInstanceOf(JiraIssue.class);
  }

  @Test
  public void doGetReturnsRightObjectHttpCodeFailCodes() throws IOException {
    url = new URL("http://localhost:" + wireMockRule.port());
    setUpCommonMocks();
    wireMockRule.givenThat(
        get(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_OK).withBody(ISSUE_RESPONSE_BODY)));
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    assertThat(restApi.doGet(JIRA_ISSUE, HTTP_OK, new int[] {HTTP_FORBIDDEN, HTTP_NOT_FOUND}))
        .isInstanceOf(JiraIssue.class);
  }

  @Test
  public void doGetReturnsInvalidResponseCode() throws IOException {
    url = new URL("http://localhost:" + wireMockRule.port());
    setUpCommonMocks();
    wireMockRule.givenThat(
        get(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_FORBIDDEN)));
    thrown.expect(IOException.class);
    thrown.expectMessage(
        "Request failed: "
            + url
            + BASE_PREFIX
            + ISSUE_CLASS_PREFIX
            + JIRA_ISSUE
            + " - "
            + HTTP_FORBIDDEN
            + " - "
            + "Forbidden");
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    restApi.doGet(JIRA_ISSUE, HTTP_OK);
  }

  @Test
  public void doGetReturnsNullIfRequestFails() throws IOException {
    url = new URL("http://localhost:" + wireMockRule.port());
    setUpCommonMocks();
    wireMockRule.givenThat(
        get(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_FORBIDDEN)));
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    assertThat(restApi.doGet(JIRA_ISSUE, HTTP_OK, new int[] {HTTP_FORBIDDEN, HTTP_NOT_FOUND}))
        .isNull();
  }

  @Test
  public void testDoPostSucceeds() throws IOException {
    url = new URL("http://localhost:" + wireMockRule.port());
    setUpCommonMocks();
    wireMockRule.givenThat(
        post(urlEqualTo(BASE_PREFIX + COMMENT_CLASS_PREFIX + JIRA_ISSUE))
            .withRequestBody(equalToJson(COMMENT_REQUEST_BODY))
            .willReturn(aResponse().withStatus(HTTP_CREATED)));
    restApi = new JiraRestApi(jiraConfig, JiraComment.class, COMMENT_CLASS_PREFIX);
    assertThat(restApi.doPost(JIRA_ISSUE, COMMENT_REQUEST_BODY, HTTP_CREATED)).isTrue();
  }

  @Test
  public void testDoPostNullBody() throws IOException {
    url = new URL("http://localhost:" + wireMockRule.port());
    setUpCommonMocks();
    wireMockRule.givenThat(
        post(urlEqualTo(BASE_PREFIX + COMMENT_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_CREATED)));
    restApi = new JiraRestApi(jiraConfig, JiraComment.class, COMMENT_CLASS_PREFIX);
    assertThat(restApi.doPost(JIRA_ISSUE, COMMENT_REQUEST_BODY, HTTP_CREATED)).isTrue();
  }

  @Test
  public void initCheckConnectivity() throws IOException {
    url = new URL("http://localhost:" + wireMockRule.port());
    setUpCommonMocks();
    wireMockRule.givenThat(
        get(urlEqualTo(BASE_PREFIX + "/serverInfo")).willReturn(aResponse().withStatus(HTTP_OK)));
    restApi = new JiraRestApi<>(url, "user", "pass");
    JiraRestApi spy = spy(restApi);
    spy.ping();
    verify(spy).doGet("", HTTP_OK);
  }
}
