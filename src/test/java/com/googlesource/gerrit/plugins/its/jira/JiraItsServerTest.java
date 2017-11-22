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
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.its.jira;

import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.git.GitRepositoryManager;
import com.google.gerrit.server.project.ProjectCache;
import org.eclipse.jgit.lib.PersonIdent;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraItsServerTest {

  @Mock private JiraConfig jiraConfig;
  @Mock private PersonIdent serverUser;
  @Mock private ProjectCache projectCache;
  @Mock private GitRepositoryManager repoManager;
  @Mock private JiraItsFacade itsFacade;
  @Mock private PluginConfig pluginConfig;

  private JiraItsServer jiraServer;
  private Project.NameKey project;
  private JiraItsServerInfo itsServerInfo;

  @Before
  public void createJiraItsServer() {
    //    jiraServer =
    //        new JiraItsServer(
    //            jiraConfig, "its-jira", serverUser, projectCache, repoManager, itsFacade);
    //    project = new Project.NameKey("project");
    //    when(jiraConfig.getPluginConfigFor(project)).thenReturn(pluginConfig);
    //  }
    //
    //  @Test
    //  public void testGetServerFromGerrit() throws MalformedURLException {
    //    JiraURL url = new JiraURL("http:\\www.jira.com");
    //    when(pluginConfig.getString(JiraConfig.PROJECT_CONFIG_URL_KEY, null))
    //        .thenReturn(null);
    //    when(pluginConfig.getString(JiraConfig.PROJECT_CONFIG_USERNAME_KEY, null))
    //        .thenReturn(null);
    //    when(pluginConfig.getString(JiraConfig.PROJECT_CONFIG_PASS_KEY, null))
    //        .thenReturn(null);
    //
    //
    // when(jiraConfig.getFromGerritConfig(JiraConfig.GERRIT_CONFIG_URL)).thenReturn(url.toString());
    //
    // when(jiraConfig.getFromGerritConfig(JiraConfig.GERRIT_CONFIG_USERNAME)).thenReturn("user");
    //
    // when(jiraConfig.getFromGerritConfig(JiraConfig.GERRIT_CONFIG_PASSWORD)).thenReturn("password");
    //    itsServerInfo =
    //        JiraItsServerInfo.builder()
    //            .url(url)
    //            .username("user")
    //            .password("pass")
    //            .build();
    //    jiraServer.getFacade(project);
    //    assertThat(itsServerInfo.getUsername()).isEqualTo("user");
  }
}
