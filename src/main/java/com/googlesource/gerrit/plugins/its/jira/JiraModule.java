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

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.googlesource.gerrit.plugins.its.base.ItsHookModule;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import com.googlesource.gerrit.plugins.its.jira.restapi.JiraRestApi;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraModule extends AbstractModule {

  private static final Logger LOG = LoggerFactory.getLogger(JiraModule.class);

  private final String pluginName;
  private final Config gerritConfig;
  private final PluginConfigFactory pluginCfgFactory;

  @Inject
  public JiraModule(
      @PluginName String pluginName,
      @GerritServerConfig Config config,
      PluginConfigFactory pluginCfgFactory) {
    this.pluginName = pluginName;
    this.gerritConfig = config;
    this.pluginCfgFactory = pluginCfgFactory;
  }

  @Override
  protected void configure() {
    if (gerritConfig.getString(pluginName, null, "url") != null) {
      LOG.info("JIRA is configured as ITS");
      bind(ItsFacade.class).to(JiraItsFacade.class).asEagerSingleton();

      install(new ItsHookModule(pluginName, pluginCfgFactory));
      install(new FactoryModuleBuilder().build(JiraRestApi.Factory.class));
    }
  }
}
