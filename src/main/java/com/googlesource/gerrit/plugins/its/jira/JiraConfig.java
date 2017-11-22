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

import com.google.common.base.Strings;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.project.NoSuchProjectException;
import com.google.inject.Inject;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The JIRA plugin configuration as read from project config. */
public class JiraConfig {
  static final String GERRIT_CONFIG_URL = "url";
  static final String GERRIT_CONFIG_USERNAME = "username";
  static final String GERRIT_CONFIG_PASSWORD = "password";
  public static final String PROJECT_CONFIG_URL_KEY = "instanceUrl";
  public static final String PROJECT_CONFIG_USERNAME_KEY = "jiraUsername";
  public static final String PROJECT_CONFIG_PASS_KEY = "password";
  private static final String COMMENTLINK = "commentlink";

  public String pluginName;
  private PluginConfigFactory cfgFactory;
  private Config gerritConfig;
  private Logger log = LoggerFactory.getLogger(JiraConfig.class);

  /**
   * Builds a JiraConfig.
   *
   * @param cfgFactory the plugin config factory
   * @param config the gerrit server config
   * @param pluginName the name of this very plugin
   */
  @Inject
  JiraConfig(
      @GerritServerConfig Config config,
      @PluginName String pluginName,
      PluginConfigFactory cfgFactory) {
    this.gerritConfig = config;
    this.pluginName = pluginName;
    this.cfgFactory = cfgFactory;
  }

  PluginConfig getPluginConfigFor(Project.NameKey projectName) {
    if (projectName != null && !Strings.isNullOrEmpty(projectName.get())) {
      try {
        return cfgFactory.getFromProjectConfigWithInheritance(projectName, pluginName);
      } catch (NoSuchProjectException e) {
        log.warn("{} not found, using global settings for {}", projectName, pluginName, e);
      }
    }
    return new PluginConfig(pluginName, new Config());
  }

  /**
   * Get config value from project configuration.
   *
   * @return config value from project config if it exists
   */
  String getFromProjectConfig(PluginConfig pluginConfig, String key) {
    return pluginConfig.getString(key, null);
  }

  /**
   * Get config value from Gerrit configuration.
   *
   * @return config value from Gerrit config if it exists
   */
  String getFromGerritConfig(String key) {
    return gerritConfig.getString(pluginName, null, key);
  }

  /**
   * Get commentLink value from Gerrit configuration.
   *
   * @return commentLink value from Gerrit config if it exists
   */
  String getCommentLinkFromGerritConfig(String key) {
    return gerritConfig.getString(COMMENTLINK, pluginName, key);
  }
}
