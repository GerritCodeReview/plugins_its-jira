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
  static final String ERROR_MSG = "Unable to load plugin %s. Cause: Wrong configuration ";
  static final String GERRIT_CONFIG_URL = "url";
  static final String GERRIT_CONFIG_USERNAME = "username";
  static final String GERRIT_CONFIG_PASSWORD = "password";
  static final String PLUGIN = "plugin";

  private final String jiraUrl;
  private final String jiraUsername;
  private final String jiraPassword;
  private final boolean isSection;

  /**
   * Builds an JiraConfig.
   *
   * @param config the gerrit server config
   * @param pluginName the name of this very plugin
   */
  @Inject
  JiraConfig(@GerritServerConfig Config config, @PluginName String pluginName) {
    isSection = config.getSections().contains(pluginName);
    jiraUrl = getConfigString(GERRIT_CONFIG_URL, config, pluginName);
    jiraUsername = getConfigString(GERRIT_CONFIG_USERNAME, config, pluginName);
    jiraPassword = getConfigString(GERRIT_CONFIG_PASSWORD, config, pluginName);
    if (jiraUrl == null || jiraUsername == null || jiraPassword == null) {
      throw new RuntimeException(format(ERROR_MSG, pluginName));
    }
  }

  /**
   * The Jira url to connect to.
   *
   * @return the jira url
   * @throws MalformedURLException
   */
  public URL getJiraUrl() throws MalformedURLException {
    URL serverUrl = new URL(jiraUrl + (jiraUrl.endsWith("/") ? "" : "/"));
    return serverUrl;
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

  /**
   * Checks if the plugin name is used to create a 'section' or 'subsection' in the config and
   * return the config value. Provides support for both formats.
   *
   * @param key the key for the value stored in config file
   * @param config the config file
   * @param pluginName name of the plugin used to get the config value
   * @return the value related to the key passed.
   */
  private String getConfigString(String key, Config config, String pluginName) {
    if (isSection) {
      return config.getString(pluginName, null, key);
    }
    return config.getString(PLUGIN, pluginName, key);
  }
}
