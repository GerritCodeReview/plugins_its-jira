
package com.googlesource.gerrit.plugins.its.jira;

import com.google.common.base.Strings;
import com.googlesource.gerrit.plugins.its.base.its.ItsServerInfo;
import java.net.MalformedURLException;
import java.net.URL;

public class JiraItsServerInfo extends ItsServerInfo {
  public static class Builder {
    public static final String ERROR_MSG = "Unable to build ItsServerInfo instance. Cause: %s ";
    private JiraItsServerInfo instance = new JiraItsServerInfo();

    private Builder() {}

    public Builder url(String url) {
      check(url, "url");
      try {
        instance.url = new URL(url);
      } catch (MalformedURLException e) {
        fail("Bad URL", e);
      }
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

    private void fail(String msg, Exception e) {
      throw new IllegalArgumentException(String.format(ERROR_MSG, msg), e);
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
