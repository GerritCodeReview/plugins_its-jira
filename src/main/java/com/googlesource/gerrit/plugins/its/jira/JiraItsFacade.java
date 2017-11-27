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

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.its.InvalidTransitionException;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraProject;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraServerInfo;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraItsFacade implements ItsFacade {

  private static final String GERRIT_CONFIG_USERNAME = "username";
  private static final String GERRIT_CONFIG_PASSWORD = "password";
  private static final String GERRIT_CONFIG_URL = "url";

  private static final int MAX_ATTEMPTS = 3;

  private Logger log = LoggerFactory.getLogger(JiraItsFacade.class);

  private final String pluginName;
  private Config gerritConfig;

  private JiraClient client;

  @Inject
  public JiraItsFacade(@PluginName String pluginName, @GerritServerConfig Config cfg) {
    this.pluginName = pluginName;
    try {
      this.gerritConfig = cfg;
      JiraServerInfo info = client().sysInfo();
      log.info(
          "Connected to JIRA at {}, reported version is {}", info.getBaseUri(), info.getVersion());
      for (JiraProject p : client().getProjects()) {
        log.info("Found project: {} (key: {})", p.getName(), p.getKey());
      }
    } catch (Exception ex) {
      log.warn("Jira is currently not available", ex);
    }
  }

  @Override
  public String healthCheck(final Check check) throws IOException {

    return execute(
        () -> {
          if (check.equals(Check.ACCESS)) return healthCheckAccess();
          return healthCheckSysinfo();
        });
  }

  @Override
  public void addComment(final String issueKey, final String comment) throws IOException {

    execute(
        () -> {
          log.debug("Adding comment {} to issue {}", comment, issueKey);
          client().addComment(issueKey, comment);
          log.debug("Added comment {} to issue {}", comment, issueKey);
          return issueKey;
        });
  }

  @Override
  public void addRelatedLink(final String issueKey, final URL relatedUrl, String description)
      throws IOException {
    addComment(
        issueKey, "Related URL: " + createLinkForWebui(relatedUrl.toExternalForm(), description));
  }

  @Override
  public void performAction(final String issueKey, final String actionName) throws IOException {

    execute(
        () -> {
          log.debug("Performing action {} on issue {}", actionName, issueKey);
          doPerformAction(issueKey, actionName);
          return issueKey;
        });
  }

  private void doPerformAction(final String issueKey, final String actionName)
      throws IOException, InvalidTransitionException {
    log.debug("Trying to perform action: {} on issue {}", actionName, issueKey);
    boolean ret = client().doTransition(issueKey, actionName);
    if (ret) {
      log.debug("Action {} successful on Issue {}", actionName, issueKey);
    } else {
      log.debug("Action {} on Issue {} not possible", actionName, issueKey);
    }
  }

  @Override
  public boolean exists(final String issueKey) throws IOException {
    return execute(() -> client().issueExists(issueKey));
  }

  private JiraClient client() throws MalformedURLException {
    if (client == null) {
      log.debug("Connecting to jira at {}", getUrl());
      client = new JiraClient(getUrl(), getUsername(), getPassword());
      log.debug("Authenticating as User {}", getUsername());
    }
    return client;
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

  private String getPassword() {
    return gerritConfig.getString(pluginName, null, GERRIT_CONFIG_PASSWORD);
  }

  private String getUsername() {
    return gerritConfig.getString(pluginName, null, GERRIT_CONFIG_USERNAME);
  }

  private String getUrl() {
    return gerritConfig.getString(pluginName, null, GERRIT_CONFIG_URL);
  }

  @Override
  public String createLinkForWebui(String url, String text) {
    return "[" + text + "|" + url + "]";
  }

  private String healthCheckAccess() throws IOException {
    client().sysInfo();
    final String result = "{\"status\"=\"ok\",\"username\"=\"" + getUsername() + "\"}";
    log.debug("Health check on access result: {}", result);
    return result;
  }

  private String healthCheckSysinfo() throws IOException {
    JiraServerInfo info = client().sysInfo();
    final String result =
        "{\"status\"=\"ok\",\"system\"=\"Jira\",\"version\"=\""
            + info.getVersion()
            + "\",\"url\"=\""
            + getUrl()
            + "\",\"build\"=\""
            + info.getBuildNumber()
            + "\"}";
    log.debug("Health check on sysinfo result: {}", result);
    return result;
  }
}
