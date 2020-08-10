// Copyright (C) 2020 The Android Open Source Project
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

import com.googlesource.gerrit.plugins.its.jira.JiraItsServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraVisibility {
  private static final Logger log = LoggerFactory.getLogger(JiraItsServerInfo.class);

  private final String type;
  private final String value;

  public JiraVisibility(JiraVisibilityType type, String value) {
    if (type != JiraVisibilityType.NOTSET && value != null) {
      this.type = type.toString();
      this.value = value;
    } else {
      if (type != JiraVisibilityType.NOTSET || value != null) {
        log.error("visibilityType and visibilityValue must be set together");
      }
      throw new IllegalArgumentException();
    }
  }

  public String getType() {
        return type;
    }

  public String getValue() {
        return value;
    }
}
