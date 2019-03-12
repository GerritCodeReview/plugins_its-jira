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

import java.util.List;

public class JiraPage<T> {

  private final String self;
  private final String nextPage;
  private final long maxResults;
  private final long startAt;
  private final long total;
  private final boolean isLast;
  private final List<T> values;

  public JiraPage(
      String self,
      String nextPage,
      long maxResults,
      long startAt,
      long total,
      boolean isLast,
      List<T> values) {
    this.self = self;
    this.nextPage = nextPage;
    this.maxResults = maxResults;
    this.startAt = startAt;
    this.total = total;
    this.isLast = isLast;
    this.values = values;
  }

  public JiraPageRequest nextPageRequest(JiraPageRequest currentPageRequest) {
    if (isLast) {
      return null;
    }
    return currentPageRequest.nextPageRequest();
  }

  public String getSelf() {
    return self;
  }

  public String getNextPage() {
    return nextPage;
  }

  public long getMaxResults() {
    return maxResults;
  }

  public long getStartAt() {
    return startAt;
  }

  public long getTotal() {
    return total;
  }

  public boolean isLast() {
    return isLast;
  }

  public List<T> getValues() {
    return values;
  }
}
