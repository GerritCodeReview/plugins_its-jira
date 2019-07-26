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
import static com.googlesource.gerrit.plugins.its.jira.JiraConfig.GERRIT_CONFIG_PASSWORD;
import static com.googlesource.gerrit.plugins.its.jira.JiraConfig.GERRIT_CONFIG_URL;
import static com.googlesource.gerrit.plugins.its.jira.JiraConfig.GERRIT_CONFIG_USERNAME;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
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
  private static final URL TEST_URL = newUrl("http://jira_example.com/");
  private static final String USER = "user";
  private static final String PLUGIN_NAME = "its-jira";

  @Rule public ExpectedException thrown = ExpectedException.none();
  @Mock private Config cfg;

  private JiraConfig jiraConfig;

  @Test
  public void gerritConfigContainsSaneValues() throws Exception {
    when(cfg.getString(PLUGIN_NAME, null, GERRIT_CONFIG_URL)).thenReturn(TEST_URL.toString());
    when(cfg.getString(PLUGIN_NAME, null, GERRIT_CONFIG_USERNAME)).thenReturn(USER);
    when(cfg.getString(PLUGIN_NAME, null, GERRIT_CONFIG_PASSWORD)).thenReturn(PASS);
    jiraConfig = new JiraConfig(cfg, PLUGIN_NAME);
    assertThat(jiraConfig.getUsername()).isEqualTo(USER);
    assertThat(jiraConfig.getPassword()).isEqualTo(PASS);
    assertThat(jiraConfig.getJiraUrl()).isEqualTo(TEST_URL);
  }

  @Test
  public void gerritConfigContainsNullValues() throws Exception {
    thrown.expect(RuntimeException.class);
    jiraConfig = new JiraConfig(cfg, PLUGIN_NAME);
  }

  private static URL newUrl(String url) {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
