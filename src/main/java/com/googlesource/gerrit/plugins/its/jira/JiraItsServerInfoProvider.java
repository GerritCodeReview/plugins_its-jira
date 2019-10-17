// Copyright (C) 2019 Android Open Source Project
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

import com.google.gerrit.entities.Project;
import com.google.inject.Inject;

public class JiraItsServerInfoProvider {

  private final JiraConfig jiraConfig;
  private final JiraItsServerCache serverCache;

  @Inject
  public JiraItsServerInfoProvider(JiraConfig jiraConfig, JiraItsServerCache serverCache) {
    this.jiraConfig = jiraConfig;
    this.serverCache = serverCache;
  }

  public JiraItsServerInfo get(Project.NameKey projectName) {
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

    return jiraItsServerInfo;
  }
}
