package com.googlesource.gerrit.plugins.its.jira.restapi;

import com.google.inject.Inject;

public class JiraRestApiProvider {
  private JiraRestApi.Factory jiraRestApiFactory;

  @Inject
  public JiraRestApiProvider(JiraRestApi.Factory jiraRestApiFactory) {
    this.jiraRestApiFactory = jiraRestApiFactory;
  }

  @SuppressWarnings("unchecked")
  public <T> JiraRestApi<T> get(Class<T> classOfT, String classPrefix) {
    return (JiraRestApi<T>) jiraRestApiFactory.create(classOfT, classPrefix);
  }

  public JiraRestApi<JiraIssue> getIssue() {
    return get(JiraIssue.class, "/issue");
  }

  public JiraRestApi<JiraServerInfo> getServerInfo() {
    return get(JiraServerInfo.class, "/serverInfo");
  }

  public JiraRestApi<JiraProject[]> getProjects() {
    return get(JiraProject[].class, "/project");
  }
}
