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

import com.google.common.base.Strings;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.api.AllProjectsConfig;
import com.google.gerrit.pgm.init.api.AllProjectsNameOnInitProvider;
import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.gerrit.pgm.init.api.InitFlags;
import com.google.gerrit.pgm.init.api.Section;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.its.base.its.InitIts;
import com.googlesource.gerrit.plugins.its.base.validation.ItsAssociationPolicy;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraServerInfo;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraServerInfoRestApi;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import org.eclipse.jgit.errors.ConfigInvalidException;

/** Initialize the GitRepositoryManager configuration section. */
@Singleton
class InitJira extends InitIts {
  private final String pluginName;
  private final Section.Factory sections;
  private final InitFlags flags;
  private Section jira;
  private URL jiraUrl;
  private String jiraUsername;
  private String jiraPassword;

  @Inject
  InitJira(
      @PluginName String pluginName,
      ConsoleUI ui,
      Section.Factory sections,
      AllProjectsConfig allProjectsConfig,
      AllProjectsNameOnInitProvider allProjects,
      InitFlags flags) {
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
          flags.cfg.setStringList(
              pluginName, null, n, Arrays.asList(flags.cfg.getStringList("jira", null, n)));
        }
        for (String n : flags.cfg.getNames(COMMENT_LINK_SECTION, "jira")) {
          flags.cfg.setStringList(
              COMMENT_LINK_SECTION,
              pluginName,
              n,
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

  private void init() throws MalformedURLException {
    this.jira = sections.get(pluginName, null);
    Section jiraComment = sections.get(COMMENT_LINK_SECTION, pluginName);

    do {
      enterJiraConnectivity();
    } while (jiraUrl != null
        && (isConnectivityRequested(jiraUrl.toString()) && !isJiraConnectSuccessful()));

    if (jiraUrl == null) {
      return;
    }

    ui.header("Jira issue-tracking association");
    jiraComment.string("Jira issue-Id regex", "match", "([A-Z]+-[0-9]+)");
    jiraComment.set("html", String.format("<a href=\"%s/browse/$1\">$1</a>", jiraUrl));

    Section pluginConfig = sections.get("plugin", pluginName);

    pluginConfig.select(
        "Issue-id enforced in commit message", "association", ItsAssociationPolicy.SUGGESTED);
  }

  public void enterJiraConnectivity() throws MalformedURLException {
    String jiraUrlString = jira.string("Jira URL (empty to skip)", "url", null);
    if (jiraUrlString != null) {
      jiraUrl = new URL(jiraUrlString);
      jiraUsername = jira.string("Jira username", "username", "");
      jiraPassword = jira.password("username", "password");
    }
  }

  private boolean isJiraConnectSuccessful() {
    ui.message("Checking Jira connectivity ... ");
    try {
      JiraServerInfo serverInfo =
          new JiraServerInfoRestApi(jiraUrl, jiraUsername, jiraPassword).get();
      if (Strings.isNullOrEmpty(serverInfo.getVersion())) {
        ui.message("*ERROR* Jira returned an empty version number");
        return false;
      }
      ui.message("[OK] - Jira Ver {}\n", serverInfo.getVersion());
      return true;
    } catch (IOException e) {
      ui.message("*FAILED* (%s)\n", e.toString());
      return false;
    }
  }
}
