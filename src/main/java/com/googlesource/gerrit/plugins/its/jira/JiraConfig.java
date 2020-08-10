// Copyright (C) 2018 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License"),
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

package com.googlesource.gerrit.plugins.its.jira;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.GerritPersonIdent;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.extensions.events.GitReferenceUpdated;
import com.google.gerrit.server.git.GitRepositoryManager;
import com.google.gerrit.server.git.meta.MetaDataUpdate;
import com.google.gerrit.server.project.CommentLinkInfoImpl;
import com.google.gerrit.server.project.NoSuchProjectException;
import com.google.gerrit.server.project.ProjectCache;
import com.google.gerrit.server.project.ProjectConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraURL;
import java.io.IOException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The JIRA plugin configuration as read from project config. */
@Singleton
public class JiraConfig {
  static final String PROJECT_CONFIG_URL_KEY = "instanceUrl";
  static final String PROJECT_CONFIG_USERNAME_KEY = "username";
  static final String PROJECT_CONFIG_PASSWORD_KEY = "password";
  static final String PROJECT_CONFIG_COMMENT_VISIBILITY_TYPE = "visibilityType";
  static final String PROJECT_CONFIG_COMMENT_VISIBILITY_VALUE = "visibilityValue";

  private static final Logger log = LoggerFactory.getLogger(JiraConfig.class);
  private static final String COMMENTLINK = "commentlink";
  private static final String GERRIT_CONFIG_URL = "url";
  private static final String GERRIT_CONFIG_USERNAME = "username";
  private static final String GERRIT_CONFIG_PASSWORD = "password";
  private static final String GERRIT_CONFIG_COMMENT_VISIBILITY_TYPE = "visibilityType";
  private static final String GERRIT_CONFIG_COMMENT_VISIBILITY_VALUE = "visibilityValue";

  private final String pluginName;
  private final PluginConfigFactory cfgFactory;
  private final Config gerritConfig;
  private final JiraItsServerInfo defaultJiraServerInfo;
  private final GitRepositoryManager repoManager;
  private final ProjectCache projectCache;
  private final ProjectConfig.Factory projectConfigFactory;
  private final PersonIdent serverUser;

  @Inject
  JiraConfig(
      @GerritServerConfig Config config,
      @PluginName String pluginName,
      PluginConfigFactory cfgFactory,
      @GerritPersonIdent PersonIdent serverUser,
      ProjectCache projectCache,
      GitRepositoryManager repoManager,
      ProjectConfig.Factory projectConfigFactory) {
    this.gerritConfig = config;
    this.pluginName = pluginName;
    this.cfgFactory = cfgFactory;
    this.serverUser = serverUser;
    this.projectCache = projectCache;
    this.repoManager = repoManager;
    this.projectConfigFactory = projectConfigFactory;
    this.defaultJiraServerInfo = buildDefaultServerInfo(gerritConfig, pluginName);
  }

  private static JiraItsServerInfo buildDefaultServerInfo(Config gerritConfig, String pluginName) {
    return JiraItsServerInfo.builder()
        .url(gerritConfig.getString(pluginName, null, GERRIT_CONFIG_URL))
        .username(gerritConfig.getString(pluginName, null, GERRIT_CONFIG_USERNAME))
        .password(gerritConfig.getString(pluginName, null, GERRIT_CONFIG_PASSWORD))
        .visibility(gerritConfig.getString(pluginName, null, GERRIT_CONFIG_COMMENT_VISIBILITY_TYPE),
                    gerritConfig.getString(pluginName, null, GERRIT_CONFIG_COMMENT_VISIBILITY_VALUE))
        .build();
  }

  JiraItsServerInfo getDefaultServerInfo() {
    return defaultJiraServerInfo;
  }

  String getCommentLinkFromGerritConfig(String key) {
    return gerritConfig.getString(COMMENTLINK, pluginName, key);
  }

  JiraItsServerInfo getServerInfoFor(String projectName) {
    PluginConfig pluginConfig = getPluginConfigFor(projectName);
    return JiraItsServerInfo.builder()
        .url(pluginConfig.getString(PROJECT_CONFIG_URL_KEY, null))
        .username(pluginConfig.getString(PROJECT_CONFIG_USERNAME_KEY, null))
        .password(pluginConfig.getString(PROJECT_CONFIG_PASSWORD_KEY, null))
        .visibility(pluginConfig.getString(PROJECT_CONFIG_COMMENT_VISIBILITY_TYPE, null),
                    pluginConfig.getString(PROJECT_CONFIG_COMMENT_VISIBILITY_VALUE, null))
        .build();
  }

  void addCommentLinksSection(Project.NameKey projectName, JiraItsServerInfo jiraItsServerInfo) {
    try (Repository git = repoManager.openRepository(projectName);
        MetaDataUpdate md = new MetaDataUpdate(GitReferenceUpdated.DISABLED, projectName, git)) {
      ProjectConfig config = projectConfigFactory.read(md);
      String link =
          CharMatcher.is('/').trimFrom(jiraItsServerInfo.getUrl().toString()) + JiraURL.URL_SUFFIX;
      if (!commentLinksExist(config, link)) {
        String match = getCommentLinkFromGerritConfig("match");
        CommentLinkInfoImpl commentlinkSection =
            new CommentLinkInfoImpl(pluginName, match, link, null, true);
        config.addCommentLinkSection(commentlinkSection);
        md.getCommitBuilder().setAuthor(serverUser);
        md.getCommitBuilder().setCommitter(serverUser);
        projectCache.evict(config.getProject());
        config.commit(md);
      }
    } catch (ConfigInvalidException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean commentLinksExist(ProjectConfig config, String link) {
    return config.getCommentLinkSections().stream().map(c -> c.link).anyMatch(link::equals);
  }

  @VisibleForTesting
  PluginConfig getPluginConfigFor(String projectName) {
    if (!Strings.isNullOrEmpty(projectName)) {
      try {
        return cfgFactory.getFromProjectConfigWithInheritance(
            new Project.NameKey(projectName), pluginName);
      } catch (NoSuchProjectException e) {
        log.warn(
            "Unable to get project configuration for {}: project '{}' not found ",
            pluginName,
            projectName,
            e);
      }
    }
    return new PluginConfig(pluginName, new Config());
  }
}
