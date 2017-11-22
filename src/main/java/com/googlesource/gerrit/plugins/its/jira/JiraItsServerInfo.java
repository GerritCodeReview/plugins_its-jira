
package com.googlesource.gerrit.plugins.its.jira;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.googlesource.gerrit.plugins.its.base.its.ItsServerInfo;

public class JiraItsServerInfo extends ItsServerInfo {
  public static class Builder {
    public static final String ERROR_MSG = "Unable to build ItsServerInfo instance. Cause: %s ";
    private JiraItsServerInfo instance = new JiraItsServerInfo();

    private Builder() {}

    public Builder url(String url) {
      check(url, "url");
      instance.url = CharMatcher.is('/').trimFrom(url) + "/";
      return this;
    }

    public Builder username(String username) {
      check(username, "username");
      instance.username = username;
      return this;
    }

    public Builder password(String password) {
      check(password, "password");
      instance.password = password;
      return this;
    }

    public JiraItsServerInfo build() {
      return instance;
    }

    private void check(String field, String msg) {
      if (Strings.isNullOrEmpty(field)) {
        fail(msg + " is null");
      }
    }

    private void fail(String msg) {
      throw new IllegalArgumentException(String.format(ERROR_MSG, msg));
    }
  }

  private String url;
  private String username;
  private String password;

  public static Builder buider() {
    return new JiraItsServerInfo.Builder();
  }

  public String getUrl() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
