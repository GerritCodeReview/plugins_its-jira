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
  private final JiraItsServerInfoProvider serverInfoProvider;
  private final JiraItsFacade itsFacade;

  @Inject
  public JiraItsServer(JiraItsServerInfoProvider serverInfoProvider, JiraItsFacade itsFacade) {
    this.serverInfoProvider = serverInfoProvider;
    this.itsFacade = itsFacade;
  }

  /**
   * Gets the server configuration from project.config. If the project config values are valid, it
   * creates a commentlinks section for "its-jira" in the project config. Returns default
   * configuration values from gerrit.config if no project config was provided. In case of invalid
   * project config, its-jira tells the user that it is not able to connect.
   *
   * @param projectName the oroject for which the Jira server configuration should be returned
   * @return the Jira server configuration for the project or the default Jira server configuration
   *     if the project does not define a project-level Jira configuration
   */
  @Override
  public JiraItsFacade getFacade(Project.NameKey projectName) {
    itsFacade.setJiraServerInstance(serverInfoProvider.get(projectName));
    return itsFacade;
  }
}
