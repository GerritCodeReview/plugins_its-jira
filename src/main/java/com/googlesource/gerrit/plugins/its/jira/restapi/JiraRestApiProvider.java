package com.googlesource.gerrit.plugins.its.jira.restapi;

import java.net.MalformedURLException;
import java.net.URL;

public class JiraRestApiProvider {
  private final URL url;
  private final String user;
  private final String pass;

  public JiraRestApiProvider(String url, String user, String pass) throws MalformedURLException {
    this.url = new URL(url + (url.endsWith("/") ? "" : "/"));
    this.user = user;
    this.pass = pass;
  }

  public <T> JiraRestApi<T> get(Class<T> classOfT, String classPrefix)
      throws MalformedURLException {
    return new JiraRestApi<>(url, user, pass, classOfT, classPrefix);
  }

  public JiraRestApi<JiraIssue> getIssue() throws MalformedURLException {
    return get(JiraIssue.class, "/issue");
  }

  public JiraRestApi<JiraServerInfo> getServerInfo() throws MalformedURLException {
    return get(JiraServerInfo.class, "/serverInfo");
  }

  public JiraRestApi<JiraProject[]> getProjects() throws MalformedURLException {
    return get(JiraProject[].class, "/project");
  }
}
