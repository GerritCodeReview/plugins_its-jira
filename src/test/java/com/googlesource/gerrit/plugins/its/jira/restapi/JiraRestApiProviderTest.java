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

package com.googlesource.gerrit.plugins.its.jira.restapi;

import static org.mockito.Mockito.verify;

import com.googlesource.gerrit.plugins.its.jira.JiraItsServerInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraRestApiProviderTest {

  @Mock private JiraRestApi.Factory jiraRestApiFactory;
  @Mock private JiraItsServerInfo server;

  private JiraRestApiProvider jiraRestApiProvider;

  @Before
  public void setUp() throws Exception {
    jiraRestApiProvider = new JiraRestApiProvider(jiraRestApiFactory);
  }

  @Test
  public void testGetIssue() {
    jiraRestApiProvider.getIssue(server);
    verify(jiraRestApiFactory).create(server, JiraIssue.class, "/issue");
  }

  @Test
  public void testGetServerInfo() {
    jiraRestApiProvider.getServerInfo(server);
    verify(jiraRestApiFactory).create(server, JiraServerInfo.class, "/serverInfo");
  }

  @Test
  public void testGetProjects() {
    jiraRestApiProvider.getProjects(server);
    verify(jiraRestApiFactory).create(server, JiraProject[].class, "/project");
  }
}
