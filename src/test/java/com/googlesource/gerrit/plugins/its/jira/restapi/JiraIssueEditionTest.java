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

import com.google.gerrit.server.OutputFormat;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.junit.Test;

import java.io.StringWriter;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 31/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class JiraIssueEditionTest {

  private static final String FIELD_ID = "fieldId";
  private static final String OPERATION = "operation";
  private static final String VALUE = "value";

  @Test
  public void testSerialization() throws Exception {
    JiraIssueEdition edition =
        JiraIssueEdition.builder().appendUpdate(FIELD_ID, OPERATION, VALUE).build();

    StringWriter stringWriter = new StringWriter();
    new JsonWriter(stringWriter)
        .beginObject()
        .name("update")
        .beginObject()
        .name(FIELD_ID)
        .beginArray()
        .beginObject()
        .name(OPERATION)
        .value(VALUE)
        .endObject()
        .endArray()
        .endObject()
        .endObject()
        .close();

    assertThat(newGson().toJson(edition)).isEqualTo(stringWriter.toString());
  }

  private Gson newGson() {
    return OutputFormat.JSON_COMPACT.newGson();
  }
}
