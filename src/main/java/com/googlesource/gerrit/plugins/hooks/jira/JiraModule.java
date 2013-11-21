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

package com.googlesource.gerrit.plugins.hooks.jira;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.hooks.ItsHookModule;
import com.googlesource.gerrit.plugins.hooks.its.ItsFacade;

public class JiraModule extends AbstractModule {

  private static final Logger LOG = LoggerFactory.getLogger(JiraModule.class);

  private final Config gerritConfig;

  @Inject
  public JiraModule(@GerritServerConfig final Config config) {
    this.gerritConfig = config;
  }

  @Override
  protected void configure() {
    if (gerritConfig.getString(JiraItsFacade.ITS_NAME_JIRA, null, "url") != null) {
      LOG.info("JIRA is configured as ITS");
      bind(ItsFacade.class).toInstance(new JiraItsFacade(gerritConfig));

      install(new ItsHookModule(JiraItsFacade.ITS_NAME_JIRA));
    }
  }
}
