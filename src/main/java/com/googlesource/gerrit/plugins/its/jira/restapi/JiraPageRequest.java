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

import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JiraPageRequest {

  private final Long startAt;
  private final Long maxResults;
  private final String orderBy;

  private JiraPageRequest(Long startAt, Long maxResults, String orderBy) {
    this.startAt = startAt;
    this.maxResults = maxResults;
    this.orderBy = orderBy;
  }

  public JiraPageRequest nextPageRequest() {
    return new JiraPageRequest(startAt + 1, maxResults, orderBy);
  }

  public String toSpec() {
    Map<String, Object> parameters = new HashMap<>();
    if (startAt != null) {
      parameters.put("startAt", startAt);
    }
    if (maxResults != null) {
      parameters.put("maxResults", maxResults);
    }
    if (!Strings.isNullOrEmpty(orderBy)) {
      parameters.put("orderBy", orderBy);
    }
    String requestParameters =
        parameters
            .entrySet()
            .stream()
            .map(parameter -> parameter.getKey() + "=" + parameter.getValue())
            .collect(Collectors.joining("&"));
    return "?" + requestParameters;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Long startAt;
    private Long maxResults;
    private String orderBy;

    private Builder() {}

    public Builder startAt(Long startAt) {
      this.startAt = startAt;
      return this;
    }

    public Builder maxResults(Long maxResults) {
      this.maxResults = maxResults;
      return this;
    }

    public Builder orderBy(String orderBy) {
      this.orderBy = orderBy;
      return this;
    }

    public JiraPageRequest build() {
      return new JiraPageRequest(startAt, maxResults, orderBy);
    }
  }
}
