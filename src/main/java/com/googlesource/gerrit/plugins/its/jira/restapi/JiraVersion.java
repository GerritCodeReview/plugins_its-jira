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

import java.text.SimpleDateFormat;
import java.util.Date;

public class JiraVersion {

  private final String id;
  private final String description;
  private final String name;
  private final boolean archived;
  private final boolean released;
  private final String releaseDate;
  private final String project;
  private final Long projectId;

  private JiraVersion(
      String id,
      String description,
      String name,
      boolean archived,
      boolean released,
      Date releaseDate,
      String project,
      Long projectId) {
    this.id = id;
    this.description = description;
    this.name = name;
    this.archived = archived;
    this.released = released;
    if (releaseDate == null) {
      this.releaseDate = null;
    } else {
      this.releaseDate = new SimpleDateFormat("yyyy-MM-dd").format(releaseDate);
    }
    this.project = project;
    this.projectId = projectId;
  }

  public String getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public boolean isArchived() {
    return archived;
  }

  public boolean isReleased() {
    return released;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public String getProject() {
    return project;
  }

  public Long getProjectId() {
    return projectId;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String id;
    private String description;
    private String name;
    private boolean archived;
    private boolean released;
    private Date releaseDate;
    private String project;
    private Long projectId;

    private Builder() {}

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder archived(boolean archived) {
      this.archived = archived;
      return this;
    }

    public Builder released(boolean released) {
      this.released = released;
      return this;
    }

    public Builder releaseDate(Date releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }

    public Builder project(String project) {
      this.project = project;
      return this;
    }

    public Builder projectId(Long projectId) {
      this.projectId = projectId;
      return this;
    }

    public JiraVersion build() {
      return new JiraVersion(
          id, description, name, archived, released, releaseDate, project, projectId);
    }
  }
}
