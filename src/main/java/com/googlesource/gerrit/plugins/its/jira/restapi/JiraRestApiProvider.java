package com.googlesource.gerrit.plugins.its.jira.restapi;

import java.net.MalformedURLException;
import java.net.URL;

public class JiraRestApiProvider {
  private final URL url;
  private final String user;
  private final String pass;

  public JiraRestApiProvider(String url, String user, String pass)
      throws MalformedURLException {
    this.url = new URL(url);
    this.user = user;
    this.pass = pass;
  }

  public <T> JiraRestApi<T> get(Class<T> classOfT, String classPrefix) {
    return new JiraRestApi<>(url, user, pass, classOfT, classPrefix);
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
