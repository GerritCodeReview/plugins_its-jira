// Copyright (C) 2018 Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License"),
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.its.jira;

import com.googlesource.gerrit.plugins.its.jira.restapi.JiraURL;
import java.net.MalformedURLException;
import java.time.Duration;

public class JiraItsServerInfo {
  public static class Builder {
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

    public Builder connectTimeout(Duration timeout) {
      instance.connectTimeout = timeout;
      return this;
    }

    public Builder readTimeout(Duration timeout) {
      instance.readTimeout = timeout;
      return this;
    }

    public JiraItsServerInfo build() {
      return instance;
    }
  }

  private JiraURL url;
  private String username;
  private String password;
  private Duration connectTimeout;
  private Duration readTimeout;

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

  public Duration getConnectTimeout() {
    return connectTimeout;
  }

  public Duration getReadTimeout() {
    return readTimeout;
  }

  public boolean isValid() {
    return url != null && username != null && password != null;
  }
}
