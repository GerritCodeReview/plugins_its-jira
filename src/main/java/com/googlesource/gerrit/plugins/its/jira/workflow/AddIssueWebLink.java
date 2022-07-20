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
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionRequest;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionType;
import com.googlesource.gerrit.plugins.its.base.workflow.CustomAction;
import com.googlesource.gerrit.plugins.its.jira.JiraClient;
import com.googlesource.gerrit.plugins.its.jira.JiraItsFacade;
import com.googlesource.gerrit.plugins.its.jira.JiraItsServerInfo;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class AddIssueWebLink implements CustomAction {

  public static final String ACTION_NAME = "add-issue-remote-link";

  private final JiraClient jiraClient;
  private final AddIssueWebLinkParametersExtractor parametersExtractor;

  @Inject
  public AddIssueWebLink(
      JiraClient jiraClient, AddIssueWebLinkParametersExtractor parametersExtractor) {
    this.jiraClient = jiraClient;
    this.parametersExtractor = parametersExtractor;
  }

  @Override
  public void execute(
      ItsFacade its, String issueKey, ActionRequest actionRequest, Map<String, String> properties)
      throws IOException {
    if (!JiraItsFacade.class.isInstance(its)) {
      throw new IllegalArgumentException("Incorrect facade type");
    }
    JiraItsFacade jits = JiraItsFacade.class.cast(its);
    JiraItsServerInfo jiraItsServerInfo = jits.getJiraServerInstance();
    Optional<AddIssueWebLinkParameters> parameters =
        parametersExtractor.extract(actionRequest, properties);
    if (!parameters.isPresent()) {
      return;
    }
    jiraClient.addIssueWebLink(
        jiraItsServerInfo,
        issueKey,
        parameters.get().getId(),
        parameters.get().getUrl(),
        parameters.get().getTitle());
  }

  @Override
  public ActionType getType() {
    return ActionType.ISSUE;
  }
}
