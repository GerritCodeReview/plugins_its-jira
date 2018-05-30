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

package com.googlesource.gerrit.plugins.its.jira.restapi;

/**
 * Created on 30/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class JiraFieldUpdate {

  private final String operation;
  private final String value;

  public JiraFieldUpdate(String operation, String value) {
    this.operation = operation;
    this.value = value;
  }

  public String getOperation() {
    return operation;
  }

  public String getValue() {
    return value;
  }
}
