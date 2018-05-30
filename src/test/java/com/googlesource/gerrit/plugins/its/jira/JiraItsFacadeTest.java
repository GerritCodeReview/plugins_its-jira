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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.googlesource.gerrit.plugins.its.base.its.InvalidTransitionException;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade.Check;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraProject;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraServerInfo;
import java.io.IOException;
import java.net.URL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraItsFacadeTest {

  private static final String ACTION = "action";
  private static final String COMMENT = "comment";
  private static final String ISSUE_KEY = "issueKey";
  private static final String FIELD_ID = "fieldId";
  private static final String VALUE = "value";

  @Mock private JiraClient jiraClient;

  private JiraItsFacade jiraFacade;

  @Before
  public void setUp() throws Exception {
    JiraServerInfo jiraServerInfo = mock(JiraServerInfo.class);
    when(jiraServerInfo.getBaseUri()).thenReturn("http://jira-server.com");
    when(jiraServerInfo.getVersion()).thenReturn("v1");
    when(jiraClient.sysInfo()).thenReturn(jiraServerInfo);
    JiraProject jiraProject = mock(JiraProject.class);
    when(jiraProject.getKey()).thenReturn("key1");
    when(jiraProject.getName()).thenReturn("testProject");
    when(jiraClient.getProjects()).thenReturn(new JiraProject[] {jiraProject});
  }

  @Test
  public void healthCheckAccess() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.healthCheck(Check.ACCESS);
    verify(jiraClient).healthCheckAccess();
  }

  @Test
  public void healthCheckSysInfo() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.healthCheck(Check.SYSINFO);
    verify(jiraClient).healthCheckSysinfo();
  }

  @Test
  public void addComment() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.addComment(ISSUE_KEY, COMMENT);
    verify(jiraClient).addComment(ISSUE_KEY, COMMENT);
  }

  @Test
  public void addRelatedLink() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.addRelatedLink(ISSUE_KEY, new URL("http://jira.com"), "description");
    verify(jiraClient).addComment(ISSUE_KEY, "Related URL: [description|http://jira.com]");
  }

  @Test
  public void addValueToField() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.addValueToField(ISSUE_KEY, VALUE, FIELD_ID);
    verify(jiraClient).addValueToField(ISSUE_KEY, VALUE, FIELD_ID);
  }

  @Test
  public void performAction() throws IOException, InvalidTransitionException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.performAction(ISSUE_KEY, ACTION);
    verify(jiraClient).doTransition(ISSUE_KEY, ACTION);
  }

  @Test
  public void exists() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.exists(ISSUE_KEY);
    verify(jiraClient).issueExists(ISSUE_KEY);
  }
}
