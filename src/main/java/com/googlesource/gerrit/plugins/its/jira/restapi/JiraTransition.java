// Copyright (C) 2017 The Android Open Source Project
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

public class JiraTransition {

  // 'Get Transactions' require a list of items
  private Item[] transitions;

  // 'Do Transaction' require a single item
  private Item transition;

  public JiraTransition(Item[] transitions) {
    this.transitions = transitions;
  }

  public JiraTransition(Item transition) {
    this.transition = transition;
  }

  public Item[] getTransitions() {
    return transitions;
  }

  public Item getTransition() {
    return transition;
  }

  public static class Item {
    private final String name;
    private final String id;

    public Item(String name, String id) {
      this.name = name;
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public String getId() {
      return id;
    }
  }
}
