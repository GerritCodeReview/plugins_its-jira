// Copyright (C) 2013 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.its.jira;

import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.its.InvalidTransitionException;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraProject;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraServerInfo;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraItsFacade implements ItsFacade {

  private static final int MAX_ATTEMPTS = 3;

  private Logger log = LoggerFactory.getLogger(JiraItsFacade.class);

  private final JiraClient jiraClient;

  @Inject
  public JiraItsFacade(JiraClient jiraClient) {
    this.jiraClient = jiraClient;
    try {
      JiraServerInfo info = this.jiraClient.sysInfo();
      log.info(
          "Connected to JIRA at {}, reported version is {}", info.getBaseUri(), info.getVersion());
      for (JiraProject p : this.jiraClient.getProjects()) {
        log.info("Found project: {} (key: {})", p.getName(), p.getKey());
      }
    } catch (Exception ex) {
      log.warn("Jira is currently not available", ex);
    }
  }

  @Override
  public String healthCheck(Check check) throws IOException {

    return execute(
        () -> {
          if (check.equals(Check.ACCESS)) {
            return jiraClient.healthCheckAccess();
          }
          return jiraClient.healthCheckSysinfo();
        });
  }

  @Override
  public void addComment(String issueKey, String comment) throws IOException {

    execute(
        () -> {
          log.debug("Adding comment {} to issue {}", comment, issueKey);
          jiraClient.addComment(issueKey, comment);
          log.debug("Added comment {} to issue {}", comment, issueKey);
          return issueKey;
        });
  }

  @Override
  public void addRelatedLink(String issueKey, URL relatedUrl, String description)
      throws IOException {
    addComment(
        issueKey, "Related URL: " + createLinkForWebui(relatedUrl.toExternalForm(), description));
  }

  @Override
  public void addValueToField(String issueKey, String value, String fieldId) throws IOException {
    execute(
        () -> {
          log.debug("Adding value {} to field {} on issue {}", value, fieldId, issueKey);
          jiraClient.addValueToField(issueKey, value, fieldId);
          return null;
        });
  }

  @Override
  public void performAction(String issueKey, String actionName) throws IOException {

    execute(
        () -> {
          log.debug("Performing action {} on issue {}", actionName, issueKey);
          doPerformAction(issueKey, actionName);
          return issueKey;
        });
  }

  private void doPerformAction(String issueKey, String actionName)
      throws IOException, InvalidTransitionException {
    log.debug("Trying to perform action: {} on issue {}", actionName, issueKey);
    boolean ret = jiraClient.doTransition(issueKey, actionName);
    if (ret) {
      log.debug("Action {} successful on Issue {}", actionName, issueKey);
    } else {
      log.debug("Action {} on Issue {} not possible", actionName, issueKey);
    }
  }

  @Override
  public boolean exists(String issueKey) throws IOException {
    return execute(() -> jiraClient.issueExists(issueKey));
  }

  private <P> P execute(Callable<P> function) throws IOException {
    int attempt = 0;
    while (true) {
      try {
        return function.call();
      } catch (Exception ex) {
        if (isRecoverable(ex) && ++attempt < MAX_ATTEMPTS) {
          log.debug("Call failed - retrying, attempt {} of {}", attempt, MAX_ATTEMPTS);
          continue;
        }
        if (ex instanceof IOException) throw ((IOException) ex);
        throw new IOException(ex);
      }
    }
  }

  private boolean isRecoverable(Exception ex) {
    String className = ex.getClass().getName();
    return className.startsWith("java.net");
  }

  @Override
  public String createLinkForWebui(String url, String text) {
    return "[" + text + "|" + url + "]";
  }
}
