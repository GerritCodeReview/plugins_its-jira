// Copyright (C) 2018 Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License"),
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.its.jira;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.GerritPersonIdent;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.extensions.events.GitReferenceUpdated;
import com.google.gerrit.server.git.GitRepositoryManager;
import com.google.gerrit.server.git.MetaDataUpdate;
import com.google.gerrit.server.git.ProjectConfig;
import com.google.gerrit.server.project.CommentLinkInfoImpl;
import com.google.gerrit.server.project.ProjectCache;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.its.ItsServer;
import com.googlesource.gerrit.plugins.its.base.its.ItsServerInfo;
import java.io.IOException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;

/**
 * Provides information about the single/current server configured. The information is tunneled back
 * to its-base to perform the its-actions.
 */
public class JiraItsServer implements ItsServer {

  private static final String URL_SUFFIX = "browse/$1";
  private static final String MATCH = "match";

  private final JiraConfig jiraConfig;
  private final PersonIdent serverUser;
  private final ProjectCache projectCache;
  private final GitRepositoryManager repoManager;
  private final String pluginName;
  private String projectUrl;
  private String projectUsername;
  private String projectPassword;

  @Inject
  public JiraItsServer(
      JiraConfig jiraConfig,
      @PluginName String pluginName,
      @GerritPersonIdent PersonIdent serverUser,
      ProjectCache projectCache,
      GitRepositoryManager repoManager) {
    this.jiraConfig = jiraConfig;
    this.pluginName = pluginName;
    this.serverUser = serverUser;
    this.projectCache = projectCache;
    this.repoManager = repoManager;
  }

  /**
   * Gets the server configuration from project.config. If the project config values are valid, it
   * creates a commentlinks section for "its-jira" in the project config. Returns default
   * configuration values from gerrit.config if no project config was provided. In case of invalid
   * project config, its-jira tells the user that it is not able to connect.
   */
  @Override
  public ItsServerInfo getServer(Project.NameKey projectName) {
    PluginConfig pluginConfig = jiraConfig.getPluginConfigFor(projectName);

    if (isProjectConfigValid(pluginConfig)) {
      addCommentLinksSection(projectName);
      return buildServerObjectWithDecryption();
    }
    return buildServerObject();
  }

  private ItsServerInfo buildServerObject() {
    return JiraItsServerInfo.buider()
        .url(projectUrl)
        .username(projectUsername)
        .password(projectPassword)
        .build();
  }

  private ItsServerInfo buildServerObjectWithDecryption() {
    return JiraItsServerInfo.buider()
        .url(projectUrl)
        .username(projectUsername)
        .password(Encrypt.decrypt(projectPassword))
        .build();
  }

  private boolean isProjectConfigValid(PluginConfig config) {
    projectUrl = jiraConfig.getFromProjectConfig(config, JiraConfig.PROJECT_CONFIG_URL_KEY);
    projectUsername =
        jiraConfig.getFromProjectConfig(config, JiraConfig.PROJECT_CONFIG_USERNAME_KEY);
    projectPassword = jiraConfig.getFromProjectConfig(config, JiraConfig.PROJECT_CONFIG_PASS_KEY);

    if (projectUrl == null && projectUsername == null && projectPassword == null) {
      projectUrl = jiraConfig.getFromGerritConfig(JiraConfig.GERRIT_CONFIG_URL);
      projectUsername = jiraConfig.getFromGerritConfig(JiraConfig.GERRIT_CONFIG_USERNAME);
      projectPassword = jiraConfig.getFromGerritConfig(JiraConfig.GERRIT_CONFIG_PASSWORD);
      return false;
    }
    return true;
  }

  private void addCommentLinksSection(Project.NameKey projectName) {
    try (Repository git = repoManager.openRepository(projectName);
        MetaDataUpdate md = new MetaDataUpdate(GitReferenceUpdated.DISABLED, projectName, git)) {
      ProjectConfig config = ProjectConfig.read(md);
      String link =
          projectUrl.endsWith("/") ? projectUrl + URL_SUFFIX : projectUrl + "/" + URL_SUFFIX;
      if (!commentLinksExist(config, link)) {
        String match = jiraConfig.getCommentLinkFromGerritConfig(MATCH);
        CommentLinkInfoImpl commentlinkSection =
            new CommentLinkInfoImpl(pluginName, match, link, null, true);
        config.addCommentLinkSection(commentlinkSection);
        md.getCommitBuilder().setAuthor(serverUser);
        md.getCommitBuilder().setCommitter(serverUser);
        projectCache.evict(config.getProject());
        config.commit(md);
      }
    } catch (ConfigInvalidException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private boolean commentLinksExist(ProjectConfig config, String link) {
    return config.getCommentLinkSections().stream().map(c -> c.link).anyMatch(link::equals);
  }
}
