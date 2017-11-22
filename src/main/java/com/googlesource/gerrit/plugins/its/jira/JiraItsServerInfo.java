
package com.googlesource.gerrit.plugins.its.jira;

import static com.googlesource.gerrit.plugins.its.jira.UrlHelper.adjustUrlPath;

import com.google.common.base.Strings;
import java.net.URL;

public class JiraItsServerInfo {
  public static class Builder {
    static final String ERROR_MSG =
        "Unable to load the plugin because of invalid configuration: %s";
    private JiraItsServerInfo instance = new JiraItsServerInfo();

    private Builder() {}

    public Builder url(URL url) {
      check(url.toString(), "url");
      instance.url = adjustUrlPath(url);
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

  private URL url;
  private String username;
  private String password;

  public static Builder buider() {
    return new JiraItsServerInfo.Builder();
  }

  public URL getUrl() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
