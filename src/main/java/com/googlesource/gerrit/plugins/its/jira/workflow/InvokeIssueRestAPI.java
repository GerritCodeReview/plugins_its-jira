// Copyright (C) 2022 The Android Open Source Project
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
import com.googlesource.gerrit.plugins.its.base.ItsPath;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionRequest;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionType;
import com.googlesource.gerrit.plugins.its.base.workflow.CustomAction;
import com.googlesource.gerrit.plugins.its.jira.JiraClient;
import com.googlesource.gerrit.plugins.its.jira.JiraItsFacade;
import com.googlesource.gerrit.plugins.its.jira.JiraItsServerInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeIssueRestAPI implements CustomAction {

  private static final Logger log = LoggerFactory.getLogger(InvokeIssueRestAPI.class);

  public static final String ACTION_NAME = "invoke-issue-restapi";

  private final JiraClient jiraClient;
  private final InvokeRestAPIParametersExtractor parametersExtractor;
  private final SoyTemplateRenderer soyTemplateRenderer;

  @Inject
  public InvokeIssueRestAPI(
      @ItsPath Path itsPath,
      JiraClient jiraClient,
      InvokeRestAPIParametersExtractor parametersExtractor) {
    this.jiraClient = jiraClient;
    this.parametersExtractor = parametersExtractor;
    this.soyTemplateRenderer = new SoyTemplateRenderer(itsPath);
  }

  @Override
  public void execute(
      ItsFacade its, String issueKey, ActionRequest actionRequest, Map<String, String> properties)
      throws IOException {
    Optional<InvokeRestAPIParameters> _parameters =
        parametersExtractor.extract(actionRequest, properties);
    if (!_parameters.isPresent()) {
      return;
    }
    InvokeRestAPIParameters parameters = _parameters.get();

    if (!JiraItsFacade.class.isInstance(its)) {
      throw new IllegalArgumentException("Incorrect facade type");
    }
    JiraItsFacade jits = JiraItsFacade.class.cast(its);
    JiraItsServerInfo jiraItsServerInfo = jits.getJiraServerInstance();

    jiraClient.invokeIssueRestAPI(
        jiraItsServerInfo,
        issueKey,
        parameters.getMethod(),
        parameters.getUri(),
        parameters.getPassCodes(),
        soyTemplateRenderer.render(parameters.getTemplate(), properties));
  }

  @Override
  public ActionType getType() {
    return ActionType.ISSUE;
  }
}
