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

package com.googlesource.gerrit.plugins.its.jira;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.api.AllProjectsConfig;
import com.google.gerrit.pgm.init.api.AllProjectsNameOnInitProvider;
import com.google.gerrit.pgm.init.api.InitFlags;
import com.google.gerrit.pgm.init.api.Section;
import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.googlesource.gerrit.plugins.hooks.its.InitIts;
import com.googlesource.gerrit.plugins.hooks.validation.ItsAssociationPolicy;

import org.eclipse.jgit.errors.ConfigInvalidException;

/** Initialize the GitRepositoryManager configuration section. */
@Singleton
class InitJira extends InitIts {
  private static final String COMMENT_LINK_SECTION = "commentLink";
  private final String pluginName;
  private final Section.Factory sections;
  private final InitFlags flags;
  private Section jira;
  private Section jiraComment;
  private String jiraUrl;
  private String jiraUsername;
  private String jiraPassword;

  @Inject
  InitJira(@PluginName String pluginName, ConsoleUI ui,
      Section.Factory sections, AllProjectsConfig allProjectsConfig,
      AllProjectsNameOnInitProvider allProjects, InitFlags flags) {
    super(pluginName, "Jira", ui, allProjectsConfig, allProjects);
    this.pluginName = pluginName;
    this.sections = sections;
    this.flags = flags;
  }

  @Override
  public void run() throws IOException, ConfigInvalidException {
    super.run();

    ui.message("\n");
    ui.header("Jira connectivity");

    if (!pluginName.equalsIgnoreCase("jira")
        && !flags.cfg.getSections().contains(pluginName)
        && flags.cfg.getSections().contains("jira")) {
      ui.message("A Jira configuration for the 'hooks-jira' plugin was found.\n");
      if (ui.yesno(true, "Copy it for the '%s' plugin?", pluginName)) {
        for (String n : flags.cfg.getNames("jira")) {
          flags.cfg.setStringList(pluginName, null, n,
              Arrays.asList(flags.cfg.getStringList("jira", null, n)));
        }
        for (String n : flags.cfg.getNames(COMMENT_LINK_SECTION, "jira")) {
          flags.cfg.setStringList(COMMENT_LINK_SECTION, pluginName, n,
              Arrays.asList(flags.cfg.getStringList(COMMENT_LINK_SECTION, "jira", n)));
        }

        if (ui.yesno(false, "Remove configuration for 'hooks-jira' plugin?")) {
          flags.cfg.unsetSection("jira", null);
          flags.cfg.unsetSection(COMMENT_LINK_SECTION, "jira");
        }
      } else {
        init();
      }
    } else {
      init();
    }
  }

  private void init() {
    this.jira = sections.get(pluginName, null);
    this.jiraComment = sections.get(COMMENT_LINK_SECTION, pluginName);

    do {
      enterJiraConnectivity();
    } while (jiraUrl != null
        && (isConnectivityRequested(jiraUrl) && !isJiraConnectSuccessful()));

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
