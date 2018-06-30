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

import com.googlesource.gerrit.plugins.its.base.its.InvalidTransitionException;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade.Check;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraItsFacadeTest {

  private static final String ACTION = "action";
  private static final String COMMENT = "comment";
  private static final String ISSUE_KEY = "issueKey";
  private static final String PROJECT_KEY = "projectKey";
  private static final String FIELD_ID = "fieldId";
  private static final String VALUE = "value";

  @Mock private JiraClient jiraClient;
  private JiraItsServerInfo server;
  private JiraItsFacade jiraFacade;

  @Test
  public void healthCheckAccess() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.healthCheck(Check.ACCESS);
    verify(jiraClient).healthCheckAccess(server);
  }

  @Test
  public void healthCheckSysInfo() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.healthCheck(Check.SYSINFO);
    verify(jiraClient).healthCheckSysinfo(server);
  }

  @Test
  public void addComment() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.addComment(ISSUE_KEY, COMMENT);
    verify(jiraClient).addComment(server, ISSUE_KEY, COMMENT);
  }

  @Test
  public void addRelatedLink() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.addRelatedLink(ISSUE_KEY, new URL("http://jira.com"), "description");
    verify(jiraClient).addComment(server, ISSUE_KEY, "Related URL: [description|http://jira.com]");
  }

  @Test
  public void addValueToField() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.addValueToField(ISSUE_KEY, VALUE, FIELD_ID);
    verify(jiraClient).addValueToField(server, ISSUE_KEY, VALUE, FIELD_ID);
  }

  @Test
  public void performAction() throws IOException, InvalidTransitionException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.performAction(ISSUE_KEY, ACTION);
    verify(jiraClient).doTransition(server, ISSUE_KEY, ACTION);
  }

  @Test
  public void createVersion() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.createVersion(PROJECT_KEY, "1.0");
    verify(jiraClient).createVersion(server, PROJECT_KEY, "1.0");
  }

  @Test
  public void exists() throws IOException {
    jiraFacade = new JiraItsFacade(jiraClient);
    jiraFacade.exists(ISSUE_KEY);
    verify(jiraClient).issueExists(server, ISSUE_KEY);
  }
}
