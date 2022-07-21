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

/** Parameters needed by {@link InvokeIssueRestAPI} and {@link InvokeProjectsRestAPI} actions */
public class InvokeRestAPIParameters {

  private final String method;
  private final String uri;
  private final int[] passCodes;
  private final String template;

  public InvokeRestAPIParameters(String method, String uri, int[] passCodes, String template) {
    this.method = method;
    this.uri = uri;
    this.passCodes = passCodes;
    this.template = template;
  }

  public String getMethod() {
    return method;
  }

  public String getUri() {
    return uri;
  }

  public int[] getPassCodes() {
    return passCodes;
  }

  public String getTemplate() {
    return template;
  }
}
