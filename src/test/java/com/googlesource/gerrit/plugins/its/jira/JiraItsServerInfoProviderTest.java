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

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.google.gerrit.entities.Project;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraItsServerInfoProviderTest {

  private static final Project.NameKey PROJECT_NAMEKEY = Project.nameKey("project");

  @Mock private JiraConfig jiraConfig;
  @Mock private JiraItsServerCache serverCache;
  @Mock private JiraItsServerInfo jiraItsServerInfo;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  private JiraItsServerInfoProvider jiraItsServerInfoProvider;

  @Test
  public void testValidServerInfoIsreturnedFromTheCache() {
    when(jiraItsServerInfo.isValid()).thenReturn(true);
    when(serverCache.get(PROJECT_NAMEKEY.get())).thenReturn(jiraItsServerInfo);
    jiraItsServerInfoProvider = new JiraItsServerInfoProvider(jiraConfig, serverCache);
    jiraItsServerInfoProvider.get(PROJECT_NAMEKEY);
    verify(jiraConfig).addCommentLinksSection(PROJECT_NAMEKEY, jiraItsServerInfo);
  }

  @Test
  public void testGetDefaultServerInfo() {
    when(jiraItsServerInfo.isValid()).thenReturn(false).thenReturn(true);
    when(serverCache.get(PROJECT_NAMEKEY.get())).thenReturn(jiraItsServerInfo);
    when(jiraConfig.getDefaultServerInfo()).thenReturn(jiraItsServerInfo);
    jiraItsServerInfoProvider = new JiraItsServerInfoProvider(jiraConfig, serverCache);
    jiraItsServerInfoProvider.get(PROJECT_NAMEKEY);
    verify(jiraConfig, never()).addCommentLinksSection(PROJECT_NAMEKEY, jiraItsServerInfo);
  }

  @Test
  public void testNoConfiguredServerInfo() {
    when(serverCache.get(PROJECT_NAMEKEY.get())).thenReturn(jiraItsServerInfo);
    when(jiraItsServerInfo.isValid()).thenReturn(false).thenReturn(false);
    when(jiraConfig.getDefaultServerInfo()).thenReturn(jiraItsServerInfo);
    jiraItsServerInfoProvider = new JiraItsServerInfoProvider(jiraConfig, serverCache);
    expectedException.expect(RuntimeException.class);
    jiraItsServerInfoProvider.get(PROJECT_NAMEKEY);
  }
}
