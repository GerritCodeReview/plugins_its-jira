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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraIssueUpdate {

  private final Map<String, List<Map<String, Object>>> update;

  private JiraIssueUpdate(Map<String, List<Map<String, Object>>> update) {
    this.update = Collections.unmodifiableMap(update);
  }

  public Map<String, List<Map<String, Object>>> getUpdate() {
    return update;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final Map<String, List<Map<String, Object>>> update;

    private Builder() {
      this.update = new HashMap<>();
    }

    public Builder appendUpdate(String fieldId, String operation, String value) {
      Object valueToPut = value;
      if ("fixVersions".equals(fieldId)) {
        valueToPut = Collections.singletonMap("name", value);
      }

      this.update
          .computeIfAbsent(fieldId, key -> new ArrayList<>())
          .add(Collections.singletonMap(operation, valueToPut));
      return this;
    }

    public JiraIssueUpdate build() {
      return new JiraIssueUpdate(update);
    }
  }
}
