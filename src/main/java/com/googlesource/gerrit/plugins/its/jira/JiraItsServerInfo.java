package com.googlesource.gerrit.plugins.its.jira;

import com.googlesource.gerrit.plugins.its.jira.restapi.JiraURL;
import java.net.MalformedURLException;

public class JiraItsServerInfo {
  public static class Builder {
    static final String ERROR_MSG = "Unable to execute because of invalid configuration: %s";
    private JiraItsServerInfo instance = new JiraItsServerInfo();

    private Builder() {}

    public Builder url(String projectUrl) {
      try {
        instance.url = projectUrl != null ? new JiraURL(projectUrl) : null;
        return this;
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException("Unable to resolve URL", e);
      }
    }

    public Builder username(String username) {
      instance.username = username;
      return this;
    }

    public Builder password(String password) {
      instance.password = password;
      return this;
    }

    public JiraItsServerInfo build() {
      return instance;
    }
  }

  private JiraURL url;
  private String username;
  private String password;

  public static Builder builder() {
    return new JiraItsServerInfo.Builder();
  }

  public JiraURL getUrl() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public boolean isValid() {
    return url != null && username != null && password != null;
  }
}
