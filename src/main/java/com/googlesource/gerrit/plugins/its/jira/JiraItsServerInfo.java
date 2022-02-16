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

import static com.google.common.base.Preconditions.checkNotNull;

import com.googlesource.gerrit.plugins.its.jira.restapi.JiraURL;
import java.net.MalformedURLException;
import java.time.Duration;

public class JiraItsServerInfo {
  public static class Builder {
    private JiraItsServerInfo instance = new JiraItsServerInfo();
    private String projectUrl;
    private Duration connectTimeout;
    private Duration readTimeout;

    private Builder() {}

    public Builder url(String projectUrl) {
      this.projectUrl = projectUrl;
      return this;
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
      this.connectTimeout = timeout;
      return this;
    }

    public Builder readTimeout(Duration timeout) {
      this.readTimeout = timeout;
      return this;
    }

    public JiraItsServerInfo build() {
      try {
        checkNotNull(connectTimeout, "Missing connection timeout");
        checkNotNull(readTimeout, "Missing read timeout");
        instance.url =
            projectUrl != null ? new JiraURL(projectUrl, connectTimeout, readTimeout) : null;
        return instance;
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException("Unable to resolve URL", e);
      }
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
