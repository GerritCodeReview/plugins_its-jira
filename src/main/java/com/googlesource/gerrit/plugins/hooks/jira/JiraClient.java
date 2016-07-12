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

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.googlesource.gerrit.plugins.hooks.its.InvalidTransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

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
   * @throws RestClientException
   */
  public Issue getIssue(String issueKey) throws RestClientException {
    IssueRestClient issueClient = this.client.getIssueClient();
    return issueClient.getIssue(issueKey).claim();
  }

  /**
   * @param issueKey Jira Issue key
   * @return true if issue exists
   */
  public boolean issueExists(String issueKey) throws RestClientException {
    boolean ret = true;
    try{
      getIssue(issueKey);
    } catch (RestClientException e) {
      if (e.getStatusCode().get() == 404){
        log.error("Issue " + issueKey + " not found ");
        ret = false;
      } else {
        throw e;
      }
    }
    return ret;
  }

  /**
   * @param issueKey Jira Issue key
   * @return Iterable of available transitions
   * @throws RestClientException
   */
  public Iterable<Transition> getTransitions(String issueKey) throws RestClientException {
    return client.getIssueClient().getTransitions(getIssue(issueKey)).claim();
  }

  /**
   * @param issueKey Jira Issue key
   * @param comment  Comment  to be added
   * @throws RestClientException
   */
  public void addComment(String issueKey, Comment comment) throws RestClientException, URISyntaxException {
    log.debug("Trying to add comment for issue " + issueKey);
    Issue issue = getIssue(issueKey);
    URI issueUri;
    issueUri = new URI(issue.getSelf().toString() + "/comment/");
    IssueRestClient issueClient = client.getIssueClient();
    Promise<Void> promise = issueClient.addComment(issueUri, comment);
    promise.claim();
    log.debug("Comment added to issue " + issueKey);
  }

  /**
   * @param issueKey   Jira Issue key
   * @param transition Transition to perform
   * @return true if successful
   */
  public boolean doTransition(String issueKey, String transition) throws RestClientException, InvalidTransitionException {
    Transition t = getTransitionByName(getTransitions(issueKey), transition);
    if (t == null) {
      throw new InvalidTransitionException("Action " + transition
        + " not executable on issue " + issueKey);
    }
    TransitionInput input;
    input = new TransitionInput(t.getId());
    log.debug("Setting transition input to: " + input.toString());
    client.getIssueClient().transition(getIssue(issueKey), input).claim();
    return true;
  }

  /**
   * @return Serverinformation of jira
   */
  public ServerInfo sysInfo() throws RestClientException {
    return client.getMetadataClient().getServerInfo().claim();
  }

  private Transition getTransitionByName(Iterable<Transition> transitions, String transition) {
    Transition ret = null;
    for (Transition t : transitions) {
      if (transition.equals(t.getName())) {
        ret = t;
        break;
      }
    }
    return ret;
  }
}
