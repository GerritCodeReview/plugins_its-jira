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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.project.NoSuchProjectException;
import org.eclipse.jgit.lib.Config;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraConfigTest {

  @Rule public ExpectedException thrown = ExpectedException.none();
  @Mock private Config cfg;
  @Mock private PluginConfigFactory cfgFactory;

  private String pluginName = "its-jira";
  private JiraConfig jiraConfig;

  @Before
  public void createJiraConfig() {
    jiraConfig = new JiraConfig(cfg, pluginName, cfgFactory);
  }

  @Test
  public void testGetPluginConfigFor() throws NoSuchProjectException {
    Project.NameKey project = new Project.NameKey("$project");
    PluginConfig pluginCfg = new PluginConfig(pluginName, new Config());
    when(cfgFactory.getFromProjectConfigWithInheritance(project, pluginName)).thenReturn(pluginCfg);
    jiraConfig.getPluginConfigFor(project);
    assertThat(pluginCfg).isNotNull();
  }
}
