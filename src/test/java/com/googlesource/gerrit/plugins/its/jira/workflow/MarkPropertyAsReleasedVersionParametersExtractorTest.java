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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.googlesource.gerrit.plugins.its.base.workflow.ActionRequest;
import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class MarkPropertyAsReleasedVersionParametersExtractorTest {

  private static final String PROPERTY_ID = "propertyId";
  private static final String PROPERTY_VALUE = "propertyValue";

  private MarkPropertyAsReleasedVersionParametersExtractor extractor;

  @Before
  public void before() {
    extractor = new MarkPropertyAsReleasedVersionParametersExtractor();
  }

  @Test
  public void testNoParameter() {
    testWrongNumberOfReceivedParameters(new String[] {});
  }

  @Test
  public void testTwoParameters() {
    testWrongNumberOfReceivedParameters(new String[] {PROPERTY_ID, PROPERTY_ID});
  }

  private void testWrongNumberOfReceivedParameters(String[] parameters) {
    ActionRequest actionRequest = mock(ActionRequest.class);
    when(actionRequest.getParameters()).thenReturn(parameters);

    Optional<MarkPropertyAsReleasedVersionParameters> extractedParameters =
        extractor.extract(actionRequest, Collections.emptyMap());
    assertFalse(extractedParameters.isPresent());
  }

  @Test
  public void testBlankPropertyId() {
    ActionRequest actionRequest = mock(ActionRequest.class);
    when(actionRequest.getParameters()).thenReturn(new String[] {""});

    Optional<MarkPropertyAsReleasedVersionParameters> extractedParameters =
        extractor.extract(actionRequest, Collections.emptyMap());
    assertFalse(extractedParameters.isPresent());
  }

  @Test
  public void testUnknownPropertyId() {
    ActionRequest actionRequest = mock(ActionRequest.class);
    when(actionRequest.getParameters()).thenReturn(new String[] {PROPERTY_ID});

    Optional<MarkPropertyAsReleasedVersionParameters> extractedParameters =
        extractor.extract(actionRequest, Collections.emptyMap());
    assertFalse(extractedParameters.isPresent());
  }

  @Test
  public void testHappyPath() {
    ActionRequest actionRequest = mock(ActionRequest.class);
    when(actionRequest.getParameters()).thenReturn(new String[] {PROPERTY_ID});

    Optional<MarkPropertyAsReleasedVersionParameters> extractedParameters =
        extractor.extract(actionRequest, Collections.singletonMap(PROPERTY_ID, PROPERTY_VALUE));
    assertTrue(extractedParameters.isPresent());
    assertEquals(PROPERTY_VALUE, extractedParameters.get().getPropertyValue());
  }
}
