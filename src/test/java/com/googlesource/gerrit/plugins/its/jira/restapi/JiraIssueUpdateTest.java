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

import static com.google.common.truth.Truth.assertThat;

import com.google.gerrit.json.OutputFormat;
import com.google.gson.Gson;
import org.junit.Test;

public class JiraIssueUpdateTest {

  private static final String FIELD_ID = "fieldId";
  private static final String OPERATION = "operation";
  private static final String VALUE = "value";

  @Test
  public void testSerialization() {
    JiraIssueUpdate issueUpdate =
        JiraIssueUpdate.builder().appendUpdate(FIELD_ID, OPERATION, VALUE).build();

    assertThat(newGson().toJson(issueUpdate))
        .isEqualTo("{\"update\":{\"fieldId\":[{\"operation\":\"value\"}]}}");
  }

  @Test
  public void testSerializationForFixVersions() {
    JiraIssueUpdate issueUpdate =
        JiraIssueUpdate.builder().appendUpdate("fixVersions", OPERATION, VALUE).build();

    assertThat(newGson().toJson(issueUpdate))
        .isEqualTo("{\"update\":{\"fixVersions\":[{\"operation\":{\"name\":\"value\"}}]}}");
  }

  private Gson newGson() {
    return OutputFormat.JSON_COMPACT.newGson();
  }
}
