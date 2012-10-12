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

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;

public class JiraClientSession {
  private final XmlRpcClient rpc;
  private String sessionId;

  public JiraClientSession(XmlRpcClient rpc, String sessionId) {
    this.rpc = rpc;
    this.sessionId = sessionId;
  }

  public void addComment(String matched, String comment) throws XmlRpcException {
    rpc.execute("jira1.addComment", new String[] { sessionId, matched, comment });
  }

  public void close() {
    // Do not really know at the moment if there should be something to release
    // on the Jira side. Cannot find at the moment a "logout" XML-RPC API for this.
  }
}