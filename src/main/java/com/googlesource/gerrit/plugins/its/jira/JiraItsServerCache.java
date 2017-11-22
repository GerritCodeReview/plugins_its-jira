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

/** Cache of project-specific Jira servers */
interface JiraItsServerCache {

  /**
   * Get the cached Jira server for a project
   *
   * @param projectName name of the project.
   * @return the cached Jira server.
   */
  JiraItsServerInfo get(String projectName);

  /**
   * Invalidate the cached Jira server for the given project.
   *
   * @param projectName project for which the Jira server is being evicted.
   */
  void evict(String projectName);
}
