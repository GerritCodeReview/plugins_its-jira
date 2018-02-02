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

import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.jira.JiraConfig;
import java.net.MalformedURLException;

public class JiraRestApiProvider {
  private JiraConfig jiraConfig;

  @Inject
  public JiraRestApiProvider(JiraConfig jiraConfig) {
    this.jiraConfig = jiraConfig;
  }

  public <T> JiraRestApi<T> get(Class<T> classOfT, String classPrefix)
      throws MalformedURLException {
    return new JiraRestApi<>(
        jiraConfig.getJiraUrl(),
        jiraConfig.getUsername(),
        jiraConfig.getPassword(),
        classOfT,
        classPrefix);
  }

  public JiraRestApi<JiraIssue> getIssue() throws MalformedURLException {
    return get(JiraIssue.class, "/issue");
  }

  public JiraRestApi<JiraServerInfo> getServerInfo() throws MalformedURLException {
    return get(JiraServerInfo.class, "/serverInfo");
  }

  public JiraRestApi<JiraProject[]> getProjects() throws MalformedURLException {
    return get(JiraProject[].class, "/project");
  }
}
