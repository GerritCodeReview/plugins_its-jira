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
import static com.googlesource.gerrit.plugins.its.jira.JiraConfig.ERROR_MSG;
import static java.lang.String.format;
import static org.mockito.Mockito.when;

import org.eclipse.jgit.lib.Config;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraConfigTest {

  private static final String PASS = "pass";
  private static final String URL = "http://jira_example.com";
  private static final String USER = "user";
  private static final String PLUGIN_NAME = "its-jira";

  @Rule public ExpectedException thrown = ExpectedException.none();
  @Mock private Config cfg;

  private JiraConfig jiraConfig;

  @Test
  public void gerritConfigContainsSaneValues() throws Exception {
    when(cfg.getString(PLUGIN_NAME, null, "username")).thenReturn(USER);
    when(cfg.getString(PLUGIN_NAME, null, "password")).thenReturn(PASS);
    when(cfg.getString(PLUGIN_NAME, null, "url")).thenReturn(URL);
    jiraConfig = new JiraConfig(cfg, PLUGIN_NAME);
    assertThat(jiraConfig.getUsername()).isEqualTo(USER);
    assertThat(jiraConfig.getPassword()).isEqualTo(PASS);
    assertThat(jiraConfig.getUrl()).isEqualTo(URL);
  }

  @Test
  public void gerritConfigContainsNullValues() throws Exception {
    thrown.expect(RuntimeException.class);
    thrown.expectMessage(format(ERROR_MSG, PLUGIN_NAME));
    jiraConfig = new JiraConfig(cfg, PLUGIN_NAME);
  }
}
