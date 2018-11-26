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

import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionRequest;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionType;
import com.googlesource.gerrit.plugins.its.base.workflow.SpecificAction;
import com.googlesource.gerrit.plugins.its.jira.JiraClient;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class MarkPropertyAsReleasedVersion implements SpecificAction {

  public static final String ACTION_NAME = "mark-property-as-released-version";

  private final JiraClient jiraClient;
  private final MarkPropertyAsReleasedVersionParametersExtractor parametersExtractor;

  @Inject
  public MarkPropertyAsReleasedVersion(
      JiraClient jiraClient, MarkPropertyAsReleasedVersionParametersExtractor parametersExtractor) {
    this.jiraClient = jiraClient;
    this.parametersExtractor = parametersExtractor;
  }

  @Override
  public void execute(
      String itsProject, ActionRequest actionRequest, Map<String, String> properties)
      throws IOException {
    Optional<MarkPropertyAsReleasedVersionParameters> parameters =
        parametersExtractor.extract(actionRequest, properties);
    if (!parameters.isPresent()) {
      return;
    }
    jiraClient.markVersionAsReleased(itsProject, parameters.get().getPropertyValue());
  }

  @Override
  public ActionType getType() {
    return ActionType.PROJECT;
  }
}