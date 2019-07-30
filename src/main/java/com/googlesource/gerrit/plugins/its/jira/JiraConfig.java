// Copyright (C) 2018 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License"),
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.its.jira;

import static com.googlesource.gerrit.plugins.its.jira.UrlHelper.*;
import static java.lang.String.format;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.jgit.lib.Config;

/** The JIRA plugin configuration as read from Gerrit config. */
@Singleton
public class JiraConfig {
  static final String ERROR_MSG = "Unable to load plugin %s because of invalid configuration: %s";
  static final String GERRIT_CONFIG_URL = "url";
  static final String GERRIT_CONFIG_USERNAME = "username";
  static final String GERRIT_CONFIG_PASSWORD = "password";

  private final URL jiraUrl;
  private final String jiraUsername;
  private final String jiraPassword;

  /**
   * Builds an JiraConfig.
   *
   * @param config the gerrit server config
   * @param pluginName the name of this very plugin
   */
  @Inject
  JiraConfig(@GerritServerConfig Config config, @PluginName String pluginName) {
    try {
      jiraUrl = adjustUrlPath(new URL(config.getString(pluginName, null, GERRIT_CONFIG_URL)));
    } catch (MalformedURLException e) {
      throw new RuntimeException(format(ERROR_MSG, pluginName, e.getLocalizedMessage()));
    }

    jiraUsername = config.getString(pluginName, null, GERRIT_CONFIG_USERNAME);
    jiraPassword = config.getString(pluginName, null, GERRIT_CONFIG_PASSWORD);
    if (jiraUrl == null || jiraUsername == null || jiraPassword == null) {
      throw new RuntimeException(format(ERROR_MSG, pluginName, "missing username/password"));
    }
  }

  /**
   * The Jira url to connect to.
   *
   * @return the jira url
   */
  public URL getJiraUrl() {
    return jiraUrl;
  }

  /**
   * The username to connect to a Jira server.
   *
   * @return the username
   */
  public String getUsername() {
    return jiraUsername;
  }

  /**
   * The password to connect to a Jira server.
   *
   * @return the password
   */
  public String getPassword() {
    return jiraPassword;
  }
}
