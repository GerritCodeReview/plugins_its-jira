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

package com.googlesource.gerrit.plugins.its.jira.workflow;

import static org.mockito.Mockito.*;

import com.googlesource.gerrit.plugins.its.base.workflow.ActionRequest;
import com.googlesource.gerrit.plugins.its.jira.JiraClient;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class MarkPropertyAsReleasedVersionTest {

  private static final String ITS_PROJECT = "test-project";
  private static final String PROPERTY_ID = "propertyId";
  private static final String PROPERTY_VALUE = "propertyValue";

  private JiraClient jiraClient;
  private MarkPropertyAsReleasedVersionParametersExtractor parametersExtractor;
  private MarkPropertyAsReleasedVersion markPropertyAsReleasedVersion;

  @Before
  public void before() {
    jiraClient = mock(JiraClient.class);
    parametersExtractor = mock(MarkPropertyAsReleasedVersionParametersExtractor.class);
    markPropertyAsReleasedVersion =
        new MarkPropertyAsReleasedVersion(jiraClient, parametersExtractor);
  }

  @Test
  public void testHappyPath() throws IOException {
    MarkPropertyAsReleasedVersionParameters extractedParameters =
        mock(MarkPropertyAsReleasedVersionParameters.class);
    when(extractedParameters.getPropertyValue()).thenReturn(PROPERTY_VALUE);

    ActionRequest actionRequest = mock(ActionRequest.class);
    Map<String, String> properties = Collections.singletonMap(PROPERTY_ID, PROPERTY_VALUE);
    when(parametersExtractor.extract(actionRequest, properties))
        .thenReturn(Optional.of(extractedParameters));

    markPropertyAsReleasedVersion.execute(ITS_PROJECT, actionRequest, properties);

    verify(jiraClient).markVersionAsReleased(ITS_PROJECT, PROPERTY_VALUE);
  }
}
