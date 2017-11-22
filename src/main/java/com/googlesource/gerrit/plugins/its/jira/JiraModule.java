// Copyright (C) 2013 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.its.jira;

import static com.googlesource.gerrit.plugins.its.jira.JiraConfig.PROJECT_CONFIG_PASS_KEY;
import static com.googlesource.gerrit.plugins.its.jira.JiraConfig.PROJECT_CONFIG_URL_KEY;
import static com.googlesource.gerrit.plugins.its.jira.JiraConfig.PROJECT_CONFIG_USERNAME_KEY;

import com.google.gerrit.extensions.annotations.Exports;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.config.ProjectConfigEntry;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.googlesource.gerrit.plugins.its.base.ItsHookModule;
import com.googlesource.gerrit.plugins.its.base.its.ItsConfig;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import com.googlesource.gerrit.plugins.its.base.its.ItsServer;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraRestApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraModule extends AbstractModule {

  private static final Logger LOG = LoggerFactory.getLogger(JiraModule.class);

  private final String pluginName;
  private final PluginConfigFactory pluginCfgFactory;

  @Inject
  public JiraModule(@PluginName String pluginName, PluginConfigFactory pluginCfgFactory) {
    this.pluginName = pluginName;
    this.pluginCfgFactory = pluginCfgFactory;
  }

  @Override
  protected void configure() {
    LOG.info("JIRA is configured as ITS");
    bind(ItsFacade.class).to(JiraItsFacade.class).asEagerSingleton();
    bind(JiraConfig.class);
    bind(ItsServer.class).to(JiraItsServer.class).asEagerSingleton();
    bind(ProjectConfigEntry.class)
        .annotatedWith(Exports.named(PROJECT_CONFIG_URL_KEY))
        .toInstance(new JiraUrlProjectConfigEntry("Server URL", ""));
    bind(ProjectConfigEntry.class)
        .annotatedWith(Exports.named(PROJECT_CONFIG_USERNAME_KEY))
        .toInstance(new ProjectConfigEntry("JIRA username", ""));
    bind(ProjectConfigEntry.class)
        .annotatedWith(Exports.named(PROJECT_CONFIG_PASS_KEY))
        .toInstance(new JiraPasswordProjectConfigEntry());
    bind(ItsConfig.class);
    install(new ItsHookModule(pluginName, pluginCfgFactory));
    install(new FactoryModuleBuilder().build(JiraRestApi.Factory.class));
  }
}
