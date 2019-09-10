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

import static com.google.common.truth.Truth.assertThat;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class JiraRestApiTest {
  private static final String ISSUE_CLASS_PREFIX = "/issue/";
  private static final String JSON_PAYLOAD = "{}";
  private static final String USERNAME = "user";
  private static final String PASSWORD = "pass";

  private JiraURL url;
  private JiraRestApi restApi;

  private void setURL(String jiraUrl) throws MalformedURLException {
    url = new JiraURL(jiraUrl);
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrl() throws Exception {
    setURL("http://jira.mycompany.com/myroot/");
    restApi = new JiraRestApi(url, USERNAME, PASSWORD, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(url.toString());
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrlNotEndingWithSlash() throws Exception {
    setURL("http://jira.mycompany.com/myroot");
    restApi = new JiraRestApi(url, USERNAME, PASSWORD, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(url.toString());
  }

  @Test
  public void testJiraServerInfoForRootJiraUrl() throws Exception {
    setURL("http://jira.mycompany.com/myroot");
    restApi = new JiraRestApi(url, USERNAME, PASSWORD, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(url.toString());
  }

  @Test
  public void testDoPut() throws Exception {
    JiraURL url = mock(JiraURL.class);
    when(url.resolveUrl(any())).thenReturn(url);
    when(url.withSpec(ISSUE_CLASS_PREFIX)).thenReturn(url);

    HttpURLConnection connection = mock(HttpURLConnection.class);
    when(url.openConnection(any())).thenReturn(connection);
    ByteArrayOutputStream connectionOutputStream = new ByteArrayOutputStream();
    when(connection.getOutputStream()).thenReturn(connectionOutputStream);
    when(connection.getResponseCode()).thenReturn(HTTP_NO_CONTENT);

    restApi = new JiraRestApi(url, USERNAME, PASSWORD, JiraIssue.class, ISSUE_CLASS_PREFIX);
    boolean pass = restApi.doPut(ISSUE_CLASS_PREFIX, JSON_PAYLOAD, HTTP_NO_CONTENT);

    verify(connection).setRequestMethod("PUT");
    verify(connection).setDoOutput(true);
    assertThat(pass).isTrue();
    assertThat(new String(connectionOutputStream.toByteArray(), "UTF-8")).isEqualTo(JSON_PAYLOAD);
  }
}
