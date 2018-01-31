package com.googlesource.gerrit.plugins.its.jira.restapi;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class JiraRestApiTest {

  @Test
  public void testJiraServerInfoForNonRootJiraUrl() throws Exception {
    String nonRootJiraUrl = "http://jira.mycompany.com/myroot/";
    JiraRestApi<JiraServerInfo> serverInfo =
        new JiraRestApiProvider(nonRootJiraUrl, "", "").getServerInfo();

    String jiraApiUrl = serverInfo.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(nonRootJiraUrl);
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrlNotEndingWithSlash() throws Exception {
    String nonRootJiraUrl = "http://jira.mycompany.com/myroot";
    JiraRestApi<JiraServerInfo> serverInfo =
        new JiraRestApiProvider(nonRootJiraUrl, "", "").getServerInfo();

    String jiraApiUrl = serverInfo.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(nonRootJiraUrl);
  }

  @Test
  public void testJiraServerInfoForRootJiraUrl() throws Exception {
    String rootJiraUrl = "http://jira.mycompany.com";
    JiraRestApi<JiraServerInfo> serverInfo =
        new JiraRestApiProvider(rootJiraUrl, "", "").getServerInfo();

    String jiraApiUrl = serverInfo.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(rootJiraUrl);
  }
}
