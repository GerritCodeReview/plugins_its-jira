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

import com.google.gerrit.reviewdb.client.Project;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacadeFactory;

/**
 * Provides information about the single/current server configured. The information is tunneled back
 * to its-base to perform the its-actions.
 */
public class JiraItsServer implements ItsFacadeFactory {
  private final JiraConfig jiraConfig;
  private final JiraItsFacade itsFacade;
  private final JiraItsServerCache serverCache;

  @Inject
  public JiraItsServer(
      JiraConfig jiraConfig, JiraItsFacade itsFacade, JiraItsServerCache serverCache) {
    this.jiraConfig = jiraConfig;
    this.itsFacade = itsFacade;
    this.serverCache = serverCache;
  }

  /**
   * Gets the server configuration from project.config. If the project config values are valid, it
   * creates a commentlinks section for "its-jira" in the project config. Returns default
   * configuration values from gerrit.config if no project config was provided. In case of invalid
   * project config, its-jira tells the user that it is not able to connect.
   */
  @Override
  public JiraItsFacade getFacade(Project.NameKey projectName) {
    JiraItsServerInfo jiraItsServerInfo = serverCache.get(projectName.get());
    if (jiraItsServerInfo.isValid()) {
      jiraConfig.addCommentLinksSection(projectName, jiraItsServerInfo);
    } else {
      jiraItsServerInfo = jiraConfig.getDefaultServerInfo();
    }

    if (!jiraItsServerInfo.isValid()) {
      throw new RuntimeException(
          String.format(
              "No valid Jira server configuration was found for project '%s' %n."
                  + "Missing one or more configuration values: url: %s, username: %s, password: %s",
              projectName.get(),
              jiraItsServerInfo.getUrl(),
              jiraItsServerInfo.getUsername(),
              jiraItsServerInfo.getPassword()));
    }

    itsFacade.setJiraServerInstance(jiraItsServerInfo);
    return itsFacade;
  }
}
