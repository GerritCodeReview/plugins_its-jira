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

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce.Renderer;
import com.googlesource.gerrit.plugins.its.base.ItsPath;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionRequest;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionType;
import com.googlesource.gerrit.plugins.its.base.workflow.CustomAction;
import com.googlesource.gerrit.plugins.its.jira.JiraClient;
import com.googlesource.gerrit.plugins.its.jira.JiraItsFacade;
import com.googlesource.gerrit.plugins.its.jira.JiraItsServerInfo;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeIssueRestAPI implements CustomAction {

  private static final Logger log = LoggerFactory.getLogger(InvokeIssueRestAPI.class);

  public static final String ACTION_NAME = "invoke-issue-restapi";

  private final Path templateDir;
  private final JiraClient jiraClient;
  private final InvokeRestAPIParametersExtractor parametersExtractor;

  @Inject
  public InvokeIssueRestAPI(
      @ItsPath Path itsPath,
      JiraClient jiraClient,
      InvokeRestAPIParametersExtractor parametersExtractor) {
    this.templateDir = itsPath.resolve("templates");
    this.jiraClient = jiraClient;
    this.parametersExtractor = parametersExtractor;
  }

  private String soyTextTemplate(
      SoyFileSet.Builder builder, String template, Map<String, String> properties) {

    Path templatePath = templateDir.resolve(template + ".soy");
    String content;

    try (Reader r = Files.newBufferedReader(templatePath, StandardCharsets.UTF_8)) {
      content = CharStreams.toString(r);
    } catch (IOException err) {
      throw new ProvisionException(
          "Failed to read template file " + templatePath.toAbsolutePath().toString(), err);
    }

    builder.add(content, templatePath.toAbsolutePath().toString());
    Renderer renderer =
        builder
            .build()
            .compileTemplates()
            .renderTemplate("etc.its.templates." + template)
            .setData(properties);
    String rendered = renderer.renderText().get();
    log.debug("Rendered template {} to:\n{}", templatePath, rendered);
    return rendered;
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

    String body = soyTextTemplate(SoyFileSet.builder(), parameters.getTemplate(), properties);

    jiraClient.invokeIssueRestAPI(
        jiraItsServerInfo,
        issueKey,
        parameters.getMethod(),
        parameters.getUri(),
        parameters.getPassCodes(),
        body);
  }

  @Override
  public ActionType getType() {
    return ActionType.ISSUE;
  }
}
