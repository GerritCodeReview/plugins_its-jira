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

import com.google.gerrit.reviewdb.client.Project;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionRequest;
import com.googlesource.gerrit.plugins.its.jira.JiraClient;
import com.googlesource.gerrit.plugins.its.jira.JiraItsServerInfo;
import com.googlesource.gerrit.plugins.its.jira.JiraItsServerInfoProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class MarkPropertyAsReleasedVersionTest {

  private static final String ITS_PROJECT = "test-project";
  private static final String PROJECT_KEY = "project";
  private static final String PROJECT_NAME = "projectName";
  private static final String PROPERTY_ID = "propertyId";
  private static final String PROPERTY_VALUE = "propertyValue";

  private ItsFacade its;
  private JiraItsServerInfo serverInfo;
  private JiraClient jiraClient;
  private MarkPropertyAsReleasedVersionParametersExtractor parametersExtractor;
  private MarkPropertyAsReleasedVersion markPropertyAsReleasedVersion;

  @Before
  public void before() {
    its = mock(ItsFacade.class);
    JiraItsServerInfoProvider serverInfoProvider = mock(JiraItsServerInfoProvider.class);
    serverInfo = mock(JiraItsServerInfo.class);
    when(serverInfoProvider.get(Project.nameKey(PROJECT_NAME))).thenReturn(serverInfo);
    jiraClient = mock(JiraClient.class);
    parametersExtractor = mock(MarkPropertyAsReleasedVersionParametersExtractor.class);
    markPropertyAsReleasedVersion =
        new MarkPropertyAsReleasedVersion(serverInfoProvider, jiraClient, parametersExtractor);
  }

  @Test
  public void testHappyPath() throws IOException {
    MarkPropertyAsReleasedVersionParameters extractedParameters =
        mock(MarkPropertyAsReleasedVersionParameters.class);
    when(extractedParameters.getPropertyValue()).thenReturn(PROPERTY_VALUE);

    ActionRequest actionRequest = mock(ActionRequest.class);
    Map<String, String> properties = buildProperties();
    when(parametersExtractor.extract(actionRequest, properties))
        .thenReturn(Optional.of(extractedParameters));

    markPropertyAsReleasedVersion.execute(its, ITS_PROJECT, actionRequest, properties);

    verify(jiraClient).markVersionAsReleased(serverInfo, ITS_PROJECT, PROPERTY_VALUE);
  }

  private Map<String, String> buildProperties() {
    Map<String, String> properties = new HashMap<>();
    properties.put(PROPERTY_ID, PROJECT_NAME);
    properties.put(PROJECT_KEY, PROJECT_NAME);
    return properties;
  }
}
