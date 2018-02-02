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
import static org.mockito.Mockito.when;

import com.googlesource.gerrit.plugins.its.jira.JiraConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class JiraRestApiTest {
  private static final String ISSUE_CLASS_PREFIX = "/issue/";

  @Mock private JiraConfig jiraConfig;

  private String url;
  private JiraRestApi restApi;

  private void setUpCommonMocks() {
    when(jiraConfig.getJiraUrl()).thenReturn(url);
    when(jiraConfig.getUsername()).thenReturn("user");
    when(jiraConfig.getPassword()).thenReturn("pass");
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrl() throws Exception {
    url = "http://jira.mycompany.com/myroot/";
    setUpCommonMocks();
    restApi =
        new JiraRestApi(
            jiraConfig.getJiraUrl(),
            jiraConfig.getUsername(),
            jiraConfig.getPassword(),
            JiraIssue.class,
            ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(url);
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrlNotEndingWithSlash() throws Exception {
    url = "http://jira.mycompany.com/myroot";
    setUpCommonMocks();
    restApi =
        new JiraRestApi(
            jiraConfig.getJiraUrl(),
            jiraConfig.getUsername(),
            jiraConfig.getPassword(),
            JiraIssue.class,
            ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(url);
  }

  @Test
  public void testJiraServerInfoForRootJiraUrl() throws Exception {
    url = "http://jira.mycompany.com";
    setUpCommonMocks();
    restApi =
        new JiraRestApi(
            jiraConfig.getJiraUrl(),
            jiraConfig.getUsername(),
            jiraConfig.getPassword(),
            JiraIssue.class,
            ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(url);
  }
}
