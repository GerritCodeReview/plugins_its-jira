package com.googlesource.gerrit.plugins.its.jira.restapi;

import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.jira.JiraItsServerInfo;

public class JiraRestApiProvider {
  private JiraRestApi.Factory jiraRestApiFactory;

  @Inject
  public JiraRestApiProvider(JiraRestApi.Factory jiraRestApiFactory) {
    this.jiraRestApiFactory = jiraRestApiFactory;
  }

  @SuppressWarnings("unchecked")
  public <T> JiraRestApi<T> get(
      JiraItsServerInfo serverInfo, Class<T> classOfT, String classPrefix) {
    return (JiraRestApi<T>) jiraRestApiFactory.create(serverInfo, classOfT, classPrefix);
  }

  public JiraRestApi<JiraIssue> getIssue(JiraItsServerInfo serverInfo) {
    return get(serverInfo, JiraIssue.class, "/issue");
  }

  public JiraRestApi<JiraServerInfo> getServerInfo(JiraItsServerInfo server) {
    return get(server, JiraServerInfo.class, "/serverInfo");
  }

  public JiraRestApi<JiraProject[]> getProjects(JiraItsServerInfo serverInfo) {
    return get(serverInfo, JiraProject[].class, "/project");
  }
}
