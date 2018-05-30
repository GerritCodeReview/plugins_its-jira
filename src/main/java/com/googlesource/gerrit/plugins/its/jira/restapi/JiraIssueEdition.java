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

package com.googlesource.gerrit.plugins.its.jira.restapi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 30/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class JiraIssueEdition {

  private final Map<String, JiraFieldUpdate> update;

  private JiraIssueEdition(Map<String, JiraFieldUpdate> update) {
    this.update = Collections.unmodifiableMap(update);
  }

  public Map<String, JiraFieldUpdate> getUpdate() {
    return update;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final Map<String, JiraFieldUpdate> update;

    private Builder() {
      this.update = new HashMap<>();
    }

    public Builder addUpdate(String fieldId, JiraFieldUpdate update) {
      this.update.put(fieldId, update);
      return this;
    }

    public JiraIssueEdition build() {
      return new JiraIssueEdition(update);
    }
  }
}
