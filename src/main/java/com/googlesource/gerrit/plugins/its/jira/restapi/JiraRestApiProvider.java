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
import com.googlesource.gerrit.plugins.its.jira.JiraItsServerInfo;

public class JiraRestApiProvider {
  private JiraRestApi.Factory jiraRestApiFactory;

  @Inject
  public JiraRestApiProvider(JiraRestApi.Factory jiraRestApiFactory) {
    this.jiraRestApiFactory = jiraRestApiFactory;
  }

  @SuppressWarnings("unchecked")
  public <T> JiraRestApi<T> get(
      JiraItsServerInfo serverInfo, Class<T> classOfT, String classPrefix) {
    return (JiraRestApi<T>) jiraRestApiFactory.create(serverInfo, classOfT, classPrefix);
  }

  public JiraRestApi<JiraIssue> getIssue(JiraItsServerInfo serverInfo) {
    return get(serverInfo, JiraIssue.class, "/issue");
  }

  public JiraRestApi<JiraServerInfo> getServerInfo(JiraItsServerInfo server) {
    return get(server, JiraServerInfo.class, "/serverInfo");
  }

  public JiraRestApi<JiraProject[]> getProjects(JiraItsServerInfo serverInfo) {
    return get(serverInfo, JiraProject[].class, "/project");
  }
}
