// Copyright (C) 2019 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
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

import com.google.common.flogger.FluentLogger;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.gerrit.server.config.AllProjectsName;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade.Check;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraURL;
import java.io.IOException;

public class JiraItsStartupHealthcheck implements LifecycleListener {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final JiraItsServer jira;
  private final AllProjectsName allProjectsName;
  private final String pluginName;

  @Inject
  public JiraItsStartupHealthcheck(
      JiraItsServer jira, AllProjectsName allProjectsName, @PluginName String pluginName) {
    this.jira = jira;
    this.allProjectsName = allProjectsName;
    this.pluginName = pluginName;
  }

  @Override
  public void start() {
    JiraItsFacade jiraFacade = jira.getFacade(allProjectsName);
    JiraURL jiraUrl = jiraFacade.getJiraServerInstance().getUrl();

    try {
      String sysInfo = jiraFacade.healthCheck(Check.SYSINFO);
      logger.atInfo().log("Connection to Jira (%s) succeeded: %s", jiraUrl, sysInfo);
    } catch (IOException e) {
      logger.atSevere().withCause(e).log(
          "%s plugin failed to start: unable to connect to Jira (%s)", pluginName, jiraUrl);
    }
  }

  @Override
  public void stop() {}
}
