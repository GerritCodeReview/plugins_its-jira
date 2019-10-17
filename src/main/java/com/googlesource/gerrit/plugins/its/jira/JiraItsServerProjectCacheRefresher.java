// Copyright (C) 2018 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
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

import com.google.gerrit.entities.RefNames;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.ProjectInfo;
import com.google.gerrit.extensions.events.GitReferenceUpdatedListener;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JiraItsServerProjectCacheRefresher implements GitReferenceUpdatedListener {
  private static final Logger log =
      LoggerFactory.getLogger(JiraItsServerProjectCacheRefresher.class);

  private final GerritApi gApi;
  private final JiraItsServerCache jiraItsServerCache;

  @Inject
  JiraItsServerProjectCacheRefresher(GerritApi gApi, JiraItsServerCache jiraItsServerCache) {
    this.gApi = gApi;
    this.jiraItsServerCache = jiraItsServerCache;
  }

  @Override
  public void onGitReferenceUpdated(Event event) {
    if (!event.getRefName().equals(RefNames.REFS_CONFIG)) {
      return;
    }
    String project = event.getProjectName();
    jiraItsServerCache.evict(project);
    try {
      for (ProjectInfo childProject : gApi.projects().name(project).children()) {
        jiraItsServerCache.evict(childProject.name);
      }
    } catch (RestApiException e) {
      log.warn("Unable to evict its-jira server cache", e);
    }
  }
}
