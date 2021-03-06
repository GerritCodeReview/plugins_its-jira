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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gerrit.entities.Project;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraItsServerTest {
  private static final Project.NameKey PROJECT_NAMEKEY = Project.nameKey("project");

  @Mock private JiraItsServerInfoProvider jiraItsserverInfoProvider;
  @Mock private JiraItsFacade itsFacade;
  @Mock private JiraItsServerInfo jiraItsServerInfo;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testGetFacade() {
    when(jiraItsserverInfoProvider.get(PROJECT_NAMEKEY)).thenReturn(jiraItsServerInfo);
    JiraItsServer jiraItsServer = new JiraItsServer(jiraItsserverInfoProvider, itsFacade);
    jiraItsServer.getFacade(PROJECT_NAMEKEY);
    verify(itsFacade).setJiraServerInstance(jiraItsServerInfo);
  }
}
