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

package com.googlesource.gerrit.plugins.hooks.jira;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import org.apache.axis.AxisFault;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rpc.soap.client.RemoteAuthenticationException;
import com.atlassian.jira.rpc.soap.client.RemoteComment;
import com.atlassian.jira.rpc.soap.client.RemoteNamedObject;
import com.atlassian.jira.rpc.soap.client.RemoteServerInfo;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;

import com.googlesource.gerrit.plugins.hooks.its.InvalidTransitionException;
import com.googlesource.gerrit.plugins.hooks.its.ItsFacade;

public class JiraItsFacade implements ItsFacade {

  private static final String GERRIT_CONFIG_USERNAME = "username";
  private static final String GERRIT_CONFIG_PASSWORD = "password";
  private static final String GERRIT_CONFIG_URL = "url";

  private static final int MAX_ATTEMPTS = 3;

  private Logger log = LoggerFactory.getLogger(JiraItsFacade.class);

  private final String pluginName;
  private Config gerritConfig;

  private JiraClient client;
  private JiraSession token;

  @Inject
  public JiraItsFacade(@PluginName String pluginName,
      @GerritServerConfig Config cfg) {
    this.pluginName = pluginName;
    try {
      this.gerritConfig = cfg;
      RemoteServerInfo info = client().getServerInfo(token);
      log.info("Connected to JIRA at " + info.getBaseUrl()
          + ", reported version is " + info.getVersion());
    } catch (Exception ex) {
      log.warn("Jira is currently not available", ex);
    }
  }

  @Override
  public String healthCheck(final Check check) throws IOException {

      return execute(new Callable<String>(){
        @Override
        public String call() throws Exception {
          if (check.equals(Check.ACCESS))
            return healthCheckAccess();
          else
            return healthCheckSysinfo();
        }});
  }

  @Override
  public void addComment(final String issueKey, final String comment) throws IOException {

    execute(new Callable<String>(){
      @Override
      public String call() throws Exception {
        log.debug("Adding comment " + comment + " to issue " + issueKey);
        RemoteComment remoteComment = new RemoteComment();
        remoteComment.setBody(comment);
        client().addComment(token, issueKey, remoteComment);
        log.debug("Added comment " + comment + " to issue " + issueKey);
        return issueKey;
      }});
  }

  @Override
  public void addRelatedLink(final String issueKey, final URL relatedUrl, String description)
      throws IOException {
    addComment(issueKey, "Related URL: " + createLinkForWebui(relatedUrl.toExternalForm(), description));
  }

  @Override
  public void performAction(final String issueKey, final String actionName)
      throws IOException {

    execute(new Callable<String>(){
      @Override
      public String call() throws Exception {
        doPerformAction(issueKey, actionName);
        return issueKey;
      }});
  }

  private void doPerformAction(final String issueKey, final String actionName)
      throws RemoteException, IOException {
    String actionId = null;
    RemoteNamedObject[] actions =
        client().getAvailableActions(token, issueKey);
    for (RemoteNamedObject action : actions) {
      if (action.getName().equalsIgnoreCase(actionName)) {
        actionId = action.getId();
      }
    }

    if (actionId != null) {
      log.debug("Executing action " + actionName + " on issue " + issueKey);
      client().performAction(token, issueKey, actionId);
    } else {
      StringBuilder sb = new StringBuilder();
      for (RemoteNamedObject action : actions) {
        if (sb.length() > 0) sb.append(',');
        sb.append('\'');
        sb.append(action.getName());
        sb.append('\'');
      }

      log.error("Action " + actionName
          + " not found within available actions: " + sb);
      throw new InvalidTransitionException("Action " + actionName
          + " not executable on issue " + issueKey);
    }
  }


  @Override
  public boolean exists(final String issueKey) throws IOException {
    return execute(new Callable<Boolean>(){
      @Override
      public Boolean call() throws Exception {
        return client().getIssue(token, issueKey) != null;
      }});
  }

  public void logout() {
    this.logout(false);
  }

  public void logout(boolean quiet) {
    try {
      client().logout(token);
    }
    catch (Exception ex) {
      if (!quiet) log.error("I was unable to logout", ex);
    }
  }

  public Object login() {
    return login(false);
  }

  public Object login(boolean quiet) {
    try {
      token = client.login(getUsername(), getPassword());
      log.info("Connected to " + getUrl() + " as " + token);
      return token;
    }
    catch (Exception ex) {
      if (!quiet) {
        log.error("I was unable to logout", ex);
      }

      return null;
    }
  }

  private JiraClient client() throws IOException {

    if (client == null) {
      try {
        log.debug("Connecting to jira at URL " + getUrl());
        client = new JiraClient(getUrl());
        log.debug("Autenthicating as user " + getUsername());
      } catch (Exception ex) {
        log.info("Unable to connect to Connected to " + getUrl() + " as "
            + getUsername());
        throw new IOException(ex);
      }

      login();
    }

    return client;
  }

  private <P> P execute(Callable<P> function) throws IOException {

    int attempt = 0;
    while(true) {
      try {
        return function.call();
      } catch (Exception ex) {
        if (isRecoverable(ex) && ++attempt < MAX_ATTEMPTS) {
          log.debug("Call failed - retrying, attempt {} of {}", attempt, MAX_ATTEMPTS);
          logout(true);
          login(true);
          continue;
        }

        if (ex instanceof IOException)
          throw ((IOException)ex);
        else
          throw new IOException(ex);
      }
    }
  }

  private boolean isRecoverable(Exception ex) {
    if (ex instanceof RemoteAuthenticationException)
      return true;

    String className = ex.getClass().getName();
    if (ex instanceof AxisFault) {
      AxisFault af = (AxisFault)ex;
      className = (af.detail == null ? "unknown" : af.detail.getClass().getName());
    }

    return className.startsWith("java.net");
  }

  private String getPassword() {
    final String pass =
        gerritConfig.getString(pluginName, null,
            GERRIT_CONFIG_PASSWORD);
    return pass;
  }

  private String getUsername() {
    final String user =
        gerritConfig.getString(pluginName, null,
            GERRIT_CONFIG_USERNAME);
    return user;
  }

  private String getUrl() {
    final String url =
        gerritConfig.getString(pluginName, null, GERRIT_CONFIG_URL);
    return url;
  }

  @Override
  public String createLinkForWebui(String url, String text) {
    return "["+text+"|"+url+"]";
  }

  private String healthCheckAccess() throws RemoteException {
    JiraClient client = new JiraClient(getUrl());
    JiraSession token = client.login(getUsername(), getPassword());
    client.logout(token);
    final String result = "{\"status\"=\"ok\",\"username\"=\""+getUsername()+"\"}";
    log.debug("Healtheck on access result: {}", result);
    return result;
  }

  private String healthCheckSysinfo() throws RemoteException, IOException {
    final RemoteServerInfo res = client().getServerInfo(token);
    final String result = "{\"status\"=\"ok\",\"system\"=\"Jira\",\"version\"=\""+res.getVersion()+"\",\"url\"=\""+getUrl()+"\",\"build\"=\""+res.getBuildNumber()+"\"}";
    log.debug("Healtheck on sysinfo result: {}", result);
    return result;
  }
}
