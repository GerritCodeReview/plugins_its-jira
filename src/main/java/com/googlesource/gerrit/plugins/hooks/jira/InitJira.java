// Copyright (C) 2013 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.hooks.jira;

import java.rmi.RemoteException;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.InitStep;
import com.google.gerrit.pgm.init.Section;
import com.google.gerrit.pgm.init.Section.Factory;
import com.google.gerrit.pgm.util.ConsoleUI;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import com.googlesource.gerrit.plugins.hooks.its.InitIts;
import com.googlesource.gerrit.plugins.hooks.validation.ItsAssociationPolicy;

/** Initialize the GitRepositoryManager configuration section. */
@Singleton
class InitJira extends InitIts implements InitStep {
  private static final String COMMENT_LINK_SECTION = "commentLink";
  private final String pluginName;
  private final ConsoleUI ui;
  private final Factory sections;
  private Section jira;
  private Section jiraComment;
  private String jiraUrl;
  private String jiraUsername;
  private String jiraPassword;

  @Inject
  InitJira(final @PluginName String pluginName, final ConsoleUI ui,
      final Injector injector, final Section.Factory sections) {
    this.pluginName = pluginName;
    this.sections = sections;
    this.ui = ui;
  }

  public void run() {
    this.jira = sections.get(pluginName, null);
    this.jiraComment = sections.get(COMMENT_LINK_SECTION, pluginName);

    ui.message("\n");
    ui.header("Jira connectivity");

    do {
      enterJiraConnectivity();
    } while (jiraUrl != null
        && (isConnectivityRequested(ui, jiraUrl) && !isJiraConnectSuccessful()));

    if (jiraUrl == null) {
      return;
    }

    ui.header("Jira issue-tracking association");
    jiraComment.string("Jira issue-Id regex", "match", "([A-Z]+-[0-9]+)");
    jiraComment.set("html",
        String.format("<a href=\"%s/browse/$1\">$1</a>", jiraUrl));
    jiraComment.select("Issue-id enforced in commit message", "association",
        ItsAssociationPolicy.SUGGESTED);
  }

  public void enterJiraConnectivity() {
    jiraUrl = jira.string("Jira URL (empty to skip)", "url", null);
    if (jiraUrl != null) {
      jiraUsername = jira.string("Jira username", "username", "");
      jiraPassword = jira.password("username", "password");
    }
  }

  private boolean isJiraConnectSuccessful() {
    ui.message("Checking Jira connectivity ... ");
    try {
      JiraClient jiraClient = new JiraClient(jiraUrl);
      JiraSession jiraToken =
          jiraClient.login(jiraUsername, jiraPassword);
      jiraClient.logout(jiraToken);
      ui.message("[OK]\n");
      return true;
    } catch (RemoteException e) {
      ui.message("*FAILED* (%s)\n", e.toString());
      return false;
    }
  }
}
