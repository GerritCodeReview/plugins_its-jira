package com.googlesource.gerrit.plugins.its.jira.restapi;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.base.CharMatcher;
import com.googlesource.gerrit.plugins.its.jira.JiraConfig;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class JiraRestApiTest {
  private static final String ISSUE_CLASS_PREFIX = "/issue/";

  @Mock private JiraConfig jiraConfig;

  private URL url;
  private JiraRestApi restApi;

  private void setUpCommonMocks(String serverUrl) throws MalformedURLException {
    url = new URL(CharMatcher.is('/').trimFrom(serverUrl) + "/");
    when(jiraConfig.getJiraUrl()).thenReturn(url);
    when(jiraConfig.getUsername()).thenReturn("user");
    when(jiraConfig.getPassword()).thenReturn("pass");
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrl() throws Exception {
    String nonRootJiraUrl = "http://jira.mycompany.com/myroot/";
    setUpCommonMocks(nonRootJiraUrl);
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(nonRootJiraUrl);
  }

  @Test
  public void testJiraServerInfoForNonRootJiraUrlNotEndingWithSlash() throws Exception {
    String nonRootJiraUrl = "http://jira.mycompany.com/myroot";
    setUpCommonMocks(nonRootJiraUrl);
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(nonRootJiraUrl);
  }

  @Test
  public void testJiraServerInfoForRootJiraUrl() throws Exception {
    String rootJiraUrl = "http://jira.mycompany.com";
    setUpCommonMocks(rootJiraUrl);
    restApi = new JiraRestApi(jiraConfig, JiraIssue.class, ISSUE_CLASS_PREFIX);
    String jiraApiUrl = restApi.getBaseUrl().toString();
    assertThat(jiraApiUrl).startsWith(rootJiraUrl);
  }
}
