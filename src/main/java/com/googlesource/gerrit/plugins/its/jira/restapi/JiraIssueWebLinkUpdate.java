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

import java.util.HashMap;
import java.util.Map;

public class JiraIssueWebLinkUpdate {

  public static Map<String, Object> getUpdate(String id, String url, String title) {
    Map<String, String> object = new HashMap<>();
    object.put("url", url);
    object.put("title", title);
    Map<String, Object> update = new HashMap<>();
    update.put("globalId", id);
    update.put("object", object);
    return update;
  }
}
