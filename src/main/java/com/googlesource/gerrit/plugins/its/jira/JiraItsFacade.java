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

import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.ServerInfo;
import com.atlassian.jira.rest.client.domain.Transition;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class JiraItsFacade implements ItsFacade {

  private static final String GERRIT_CONFIG_USERNAME = "username";
  private static final String GERRIT_CONFIG_PASSWORD = "password";
  private static final String GERRIT_CONFIG_URL = "url";

  private Logger log = LoggerFactory.getLogger(JiraItsFacade.class);

  private final String pluginName;
  private Config gerritConfig;

  private JiraClient client;

  @Inject
  public JiraItsFacade(@PluginName String pluginName,
                       @GerritServerConfig Config cfg) {
    this.pluginName = pluginName;
    try {
      this.gerritConfig = cfg;
      ServerInfo info = client().sysInfo();
      log.info("Connected to JIRA at {}, reported version is {}", info.getBaseUri(), info.getVersion());
    } catch (Exception ex) {
      log.warn("Jira is currently not available", ex);
    }
  }

  @Override
  public String healthCheck(final Check check) throws IOException {
    if (check.equals(Check.ACCESS))
      return healthCheckAccess();
    else
      return healthCheckSysinfo();
  }

  @Override
  public void addComment(final String issueKey, final String comment) throws IOException {
    try {
      log.debug("Adding comment {} to issue {}", comment, issueKey);
      client().addComment(issueKey, Comment.valueOf(comment));
      log.debug("Added comment {} to issue {}", comment, issueKey);
    } catch (IOException e) {
      log.error("Failed to add comment {} to issue {}", comment, issueKey);
    }
  }

  @Override
  public void addRelatedLink(final String issueKey, final URL relatedUrl, String description)
      throws IOException {
    addComment(issueKey, "Related URL: " + createLinkForWebui(relatedUrl.toExternalForm(), description));
  }

  @Override
  public void performAction(final String issueKey, final String actionName)
      throws IOException {
    try {
      log.debug("Performing action {} on issue {}", actionName, issueKey);
      doPerformAction(issueKey, actionName);
    } catch (IOException e) {
      log.error("Failed to perform action {} on issue {}", actionName, issueKey);
      throw new IOException(e);
    }
  }

  private void doPerformAction(final String issueKey, final String actionName)
    throws IOException {
    log.debug("Trying to perform action: " + actionName + " on issue " + issueKey);
    try {
      boolean ret = client().doTransition(issueKey, actionName);
      if (ret) {
        log.debug("Action " + actionName + " successful on Issue " + issueKey);
      } else {
        log.debug("Transitioning to current status not possible");
      }
    } catch (IOException e) {
      StringBuilder sb = new StringBuilder();
      for (Transition t : client().getTransitions(issueKey)) {
        if (sb.length() > 0) {
          sb.append(',');
        }
        sb.append('\'');
        sb.append(t.getName());
        sb.append('\'');
      }
      log.error("Available Transitions: " + sb);
      throw e;
    }
  }


  @Override
  public boolean exists(final String issueKey) throws IOException {
    return client().issueExists(issueKey);
  }

  private JiraClient client() throws IOException {
    if (client == null) {
      try {
        log.debug("Connecting to jira at {}", getUrl());
        client = new JiraClient(getUrl(), getUsername(), getPassword());
        log.debug("Authenticating as User {}", getUsername());
      } catch (URISyntaxException e) {
        log.error("Connecting failed at {} as user {}", getUrl(), getUsername());
        throw new IOException(e);
      }
    }
    return client;
  }

  private String getPassword() {
    return gerritConfig.getString(pluginName, null,
      GERRIT_CONFIG_PASSWORD);
  }

  private String getUsername() {
    return gerritConfig.getString(pluginName, null,
      GERRIT_CONFIG_USERNAME);
  }

  private String getUrl() {
    return gerritConfig.getString(pluginName, null, GERRIT_CONFIG_URL);
  }

  @Override
  public String createLinkForWebui(String url, String text) {
    return "[" + text + "|" + url + "]";
  }

  private String healthCheckAccess() throws IOException {
    try {
      new JiraClient(getUrl(), getUsername(), getPassword()).sysInfo();
    } catch (URISyntaxException e) {
      throw new IOException(e);
    }
    final String result = "{\"status\"=\"ok\",\"username\"=\"" + getUsername() + "\"}";
    log.debug("Healtheck on access result: {}", result);
    return result;
  }

  private String healthCheckSysinfo() throws IOException {
    ServerInfo info = client().sysInfo();
    final String result = "{\"status\"=\"ok\",\"system\"=\"Jira\",\"version\"=\"" + info.getVersion() + "\",\"url\"=\"" + getUrl() + "\",\"build\"=\"" + info.getBuildNumber() + "\"}";
    log.debug("Healtheck on sysinfo result: {}", result);
    return result;
  }
}
