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

import com.google.common.base.CharMatcher;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class JiraRestApiTest {
  private static final String ISSUE_CLASS_PREFIX = "/issue/";

  private URL url;
  private String user = "user";
  private String password = "pass";
  private JiraRestApi restApi;

  private void setURL(String jiraUrl) throws MalformedURLException {
    url = new URL(CharMatcher.is('/').trimFrom(jiraUrl) + "/");
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrl() throws Exception {
    setURL("http://jira.mycompany.com/myroot/");
    restApi = new JiraRestApi(url, user, password, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(url.toString());
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrlNotEndingWithSlash() throws Exception {
    setURL("http://jira.mycompany.com/myroot");
    restApi = new JiraRestApi(url, user, password, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(url.toString());
  }

  @Test
  public void testJiraServerInfoForRootJiraUrl() throws Exception {
    setURL("http://jira.mycompany.com");
    restApi = new JiraRestApi(url, user, password, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(url.toString());
  }
}
