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

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.ServerInfo;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * Jira Rest Client.
 */
public class JiraClient {
  private static final Logger log = LoggerFactory.getLogger(JiraClient.class);

  private JiraRestClient client = null;

  /**
   * @param url  jira url
   * @param user username of the jira user
   * @param pass password of the jira user
   * @throws URISyntaxException
   */
  public JiraClient(String url, String user, String pass) throws URISyntaxException {
    URI jiraUri = new URI(url);
    log.debug("Trying to access Jira at " + jiraUri);
    JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
    client = factory.createWithBasicHttpAuthentication(jiraUri, user, pass);
    log.debug("Initialized jira client " + client);
  }

  /**
   * @param issueKey Issue id
   * @return Issueobjekt by issue id
   * @throws IOException
   */
  public Issue getIssue(String issueKey) throws IOException {
    IssueRestClient issueClient = this.client.getIssueClient();
    Promise<Issue> promise = issueClient.getIssue(issueKey);
    try {
      return promise.claim();
    } catch (Exception e) {
      log.error("Failed to get issue by issuekey " + issueKey);
      throw new IOException(e);
    }
  }

  /**
   * @param issueKey Jira Issue key
   * @return true if issue exists
   */
  public boolean issueExists(String issueKey) {
    try {
      getIssue(issueKey);
      return true;
    } catch (IOException e) {
      log.error("Issue " + issueKey + " not found " + e.getCause().getMessage());
      return false;
    }
  }

  /**
   * @param issueKey Jira Issue key
   * @return Iterable of available transitions
   * @throws IOException
   */
  public Iterable<Transition> getTransitions(String issueKey) throws IOException {
    try {
      return client.getIssueClient().getTransitions(getIssue(issueKey)).get();
    } catch (Exception e) {
      log.error("Failed to retrieve transitions of issue " + issueKey);
      throw new IOException("Transitions error", e);
    }
  }

  /**
   * @param issueKey Jira Issue key
   * @param comment  Comment  to be added
   * @throws IOException
   */
  public void addComment(String issueKey, Comment comment) throws IOException {
    try {
      log.debug("Trying to add comment for issue " + issueKey);
      Issue issue = getIssue(issueKey);
      URI issueUri = new URI(issue.getSelf().toString() + "/comment/");
      IssueRestClient issueClient = client.getIssueClient();
      Promise<Void> promise = issueClient.addComment(issueUri, comment);
      promise.claim();
      log.debug("Comment added to issue " + issueKey);
    } catch (Exception e) {
      log.error("Could not add comment to issue " + issueKey);
      throw new IOException("Adding Comment to issue " + issueKey + " failed", e);
    }
  }

  /**
   * @param issueKey   Jira Issue key
   * @param transition Transition to perform
   * @return true if successful
   */
  public boolean doTransition(String issueKey, String transition) {
    Transition t;
    boolean result = false;
    try {
      t = getTransitionByName(getTransitions(issueKey), transition);
      TransitionInput input;
      input = new TransitionInput(t.getId());
      log.debug("Setting transition input to: " + input.toString());
      client.getIssueClient().transition(getIssue(issueKey), input).claim();
      result = true;
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return result;
  }

  /**
   * @return Serverinformation of jira
   * @throws IOException
   */
  public ServerInfo sysInfo() throws IOException {
    try {
      return client.getMetadataClient().getServerInfo().get();
    } catch (InterruptedException e) {
      log.error("Serverinfo request interrupted");
      throw new IOException(e);
    } catch (ExecutionException e) {
      log.error(e.getMessage());
      throw new IOException(e);
    }
  }

  private Transition getTransitionByName(Iterable<Transition> transitions, String transition) throws IOException {
    for (Transition t : transitions) {
      if (transition.equals(t.getName())) {
        return t;
      }
    }
    throw new IOException("No matching transition found");
  }
}
