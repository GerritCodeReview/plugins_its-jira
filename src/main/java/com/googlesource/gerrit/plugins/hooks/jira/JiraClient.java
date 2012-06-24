// Copyright (C) 2012 The Android Open Source Project
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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Listen
@Singleton
public class JiraClient implements LifecycleListener {
  private static Logger log = LoggerFactory.getLogger(JiraClient.class);

  private final String username;
  private final String password;
  private final XmlRpcClientConfigImpl rpcConfig;
  private final XmlRpcClient rpcClient;

  @Inject
  public JiraClient(JiraPluginConfig config) throws MalformedURLException {
    this(config.rpcUrl, config.username, config.password);
  }

  public JiraClient(final String rpcUrl, final String username,
      final String password) throws MalformedURLException {
    this.username = username;
    this.password = password;
    rpcConfig = new XmlRpcClientConfigImpl();
    rpcConfig.setServerURL(new URL(rpcUrl));
    rpcClient = new XmlRpcClient();
    rpcClient.setConfig(rpcConfig);
  }

  public JiraClientSession newSession() throws XmlRpcException {
    return new JiraClientSession(this.rpcClient, (String) rpcClient.execute(
        "jira1.login", new Object[] {username, password}));
  }

  public void start() {
      try {
        JiraClientSession session = newSession();
        session.close();
      } catch (XmlRpcException e) {
        log.error("Cannot validate Jira configuration and connectivity", e);
        throw new RuntimeException(e);
      }
  }

  public void stop() {
  }

}
