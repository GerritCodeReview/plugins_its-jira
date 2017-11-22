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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.google.gerrit.reviewdb.client.Project;

@RunWith(MockitoJUnitRunner.class)
public class JiraItsServerTest {
  private static final Project.NameKey PROJECT_NAMEKEY = new Project.NameKey("project");

  @Mock private JiraConfig jiraConfig;
  @Mock private JiraItsFacade itsFacade;
  @Mock private JiraItsServerCache serverCache;
  @Mock private JiraItsServerInfo jiraItsServerInfo;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  private JiraItsServer jiraItsServer;


  @Test
  public void testValidServerInfoIsreturnedFromTheCache() throws Exception {
    when(jiraItsServerInfo.isValid()).thenReturn(true);
    when(serverCache.get(PROJECT_NAMEKEY.get())).thenReturn(jiraItsServerInfo);
    jiraItsServer = new JiraItsServer(jiraConfig, itsFacade, serverCache);
    jiraItsServer.getFacade(PROJECT_NAMEKEY);
    verify(jiraConfig).addCommentLinksSection(PROJECT_NAMEKEY, jiraItsServerInfo);
    verify(itsFacade).setJiraServerInstance(jiraItsServerInfo);
  }

  @Test
  public void testGetDefaultServerInfo() throws Exception {
    when(jiraItsServerInfo.isValid()).thenReturn(false).thenReturn(true);
    when(serverCache.get(PROJECT_NAMEKEY.get())).thenReturn(jiraItsServerInfo);
    when(jiraConfig.getDefaultServerInfo()).thenReturn(jiraItsServerInfo);
    jiraItsServer = new JiraItsServer(jiraConfig, itsFacade, serverCache);
    jiraItsServer.getFacade(PROJECT_NAMEKEY);
    verify(jiraConfig, never()).addCommentLinksSection(PROJECT_NAMEKEY, jiraItsServerInfo);
    verify(itsFacade).setJiraServerInstance(jiraItsServerInfo);
  }

  @Test
  public void testNoConfiguredServerInfo() throws Exception {
    when(serverCache.get(PROJECT_NAMEKEY.get())).thenReturn(jiraItsServerInfo);
    when(jiraItsServerInfo.isValid()).thenReturn(false).thenReturn(false);
    when(jiraConfig.getDefaultServerInfo()).thenReturn(jiraItsServerInfo);
    jiraItsServer = new JiraItsServer(jiraConfig, itsFacade, serverCache);
    String expectedMessage = String.format(
        "No valid Jira server configuration was found for project '%s' %n.", PROJECT_NAMEKEY.get());
    expectedException.expectMessage(expectedMessage);
    expectedException.expect(RuntimeException.class);
    jiraItsServer.getFacade(PROJECT_NAMEKEY);
  }
}
