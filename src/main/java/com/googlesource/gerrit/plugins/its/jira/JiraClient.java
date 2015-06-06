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

import java.net.URL;
import java.rmi.RemoteException;

import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.JiraSoapServiceServiceLocator;
import com.atlassian.jira.rpc.soap.client.RemoteComment;
import com.atlassian.jira.rpc.soap.client.RemoteFieldValue;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteNamedObject;
import com.atlassian.jira.rpc.soap.client.RemoteServerInfo;

public class JiraClient {

  private final JiraSoapService service;

  public JiraClient(final String baseUrl) throws RemoteException {
    this(baseUrl, "/rpc/soap/jirasoapservice-v2");
  }

  public JiraClient(final String baseUrl, final String rpcPath) throws RemoteException {
    try {
      JiraSoapServiceServiceLocator locator = new JiraSoapServiceServiceLocator();
      service = locator.getJirasoapserviceV2(new URL(baseUrl+rpcPath));
    }
    catch (Exception e) {
        throw new RemoteException("ServiceException during SOAPClient contruction", e);
    }
  }

  public JiraSession login(final String username, final String password) throws RemoteException {
    String token = service.login(username, password);
    return new JiraSession(username, token);
  }

  public void logout(JiraSession token) throws RemoteException {
    service.logout(getToken(token));
  }

  public RemoteIssue getIssue(JiraSession token, String issueKey) throws RemoteException {
    return service.getIssue(getToken(token), issueKey);
  }

  public RemoteNamedObject[] getAvailableActions(JiraSession token, String issueKey) throws RemoteException {
    return service.getAvailableActions(getToken(token), issueKey);
  }

  public RemoteIssue performAction(JiraSession token, String issueKey, String actionId, RemoteFieldValue... params) throws RemoteException {
    return service.progressWorkflowAction(getToken(token), issueKey, actionId, params);
  }

  public void addComment(JiraSession token, String issueKey, RemoteComment comment) throws RemoteException {
    service.addComment(getToken(token), issueKey, comment);
  }

  public RemoteServerInfo getServerInfo(JiraSession token) throws RemoteException {
    return service.getServerInfo(getToken(token));
  }

  private String getToken(JiraSession token) {
    return token == null ? null : token.getToken();
  }

}
