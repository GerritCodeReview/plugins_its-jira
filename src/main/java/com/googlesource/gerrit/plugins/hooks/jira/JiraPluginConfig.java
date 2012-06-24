// Copyright (C) 2012 The Android Open Source Project
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
package com.googlesource.gerrit.plugins.hooks.jira;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.SitePath;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class JiraPluginConfig {
  private static final Logger log = LoggerFactory.getLogger(JiraPluginConfig.class);

  private final Config config;
  private final File gerritSite;

  public final String username;
  public final String password;
  public final String rpcUrl;
  public final File gitBasePath;
  public final String issueRegex;
  public final String gitwebUrl;

  @Inject
  public JiraPluginConfig(@GerritServerConfig Config gerritConfig,
      @SitePath File gerritSitePath) throws InvalidConfigurationException, MalformedURLException {
    this.config = gerritConfig;
    this.gerritSite = gerritSitePath;
    this.username = getStringConfig("username");
    this.password = getStringConfig("password");
    this.rpcUrl = getStringConfig("rpcUrl");
    this.gitBasePath =
        new File(gerritSite, getStringConfigWithDefault("gerrit", "basePath", "git"));
    this.issueRegex =
        getStringConfigWithDefault("commentLink", "jira", "match",
            "(\\[[a-zA-Z0-9]*-[0-9]*\\])");
    this.gitwebUrl = getGitwebUrl(gerritConfig);
  }

  private String getGitwebUrl(Config gerritConfig) throws MalformedURLException {
    String webUrl = gerritConfig.getString("gitweb", null, "url");
    if (webUrl == null) {
      if (gerritConfig.getString("gitweb", null, "cgi") != null) {
        String canonicalUrl =
            gerritConfig.getString("gerrit", null, "canonicalWebUrl");
        webUrl = new URL(new URL(canonicalUrl), "gitweb").toExternalForm();
      }
    }
    return webUrl;
  }

  private String getStringConfigWithDefault(String section, String key,
      String defaultValue) {
    return Objects.firstNonNull(config.getString(section, null, key),
        defaultValue);
  }

  private String getStringConfigWithDefault(String section, String subsection,
      String key, String defaultValue) {
    return Objects.firstNonNull(config.getString(section, subsection, key),
        defaultValue);
  }

  private String getStringConfig(String name) throws InvalidConfigurationException {
    String value = config.getString("jira", null, name);
    if (value == null)
      throw new InvalidConfigurationException(
          "Missing required configuration parameter jira." + name);
    return value;
  }
}
