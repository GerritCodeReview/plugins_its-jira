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

package com.googlesource.gerrit.plugins.its.jira;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gerrit.extensions.events.GitReferenceUpdatedListener;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.server.cache.CacheModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JiraItsServerCacheImpl implements JiraItsServerCache {
  private static final Logger log = LoggerFactory.getLogger(JiraItsServerCacheImpl.class);
  private static final String CACHE_NAME = "jira_server_project";

  private final LoadingCache<String, JiraItsServerInfo> cache;

  @Inject
  JiraItsServerCacheImpl(@Named(CACHE_NAME) LoadingCache<String, JiraItsServerInfo> cache) {
    this.cache = cache;
  }

  @Override
  public JiraItsServerInfo get(String projectName) {
    try {
      return cache.get(projectName);
    } catch (ExecutionException e) {
      log.warn("Cannot get project specific rules for project {}", projectName, e);
      return JiraItsServerInfo.builder().url(null).username(null).password(null).build();
    }
  }

  @Override
  public void evict(String projectName) {
    cache.invalidate(projectName);
  }

  public static Module module() {
    return new CacheModule() {
      @Override
      protected void configure() {
        cache(CACHE_NAME, String.class, JiraItsServerInfo.class).loader(Loader.class);
        bind(JiraItsServerCacheImpl.class);
        bind(JiraItsServerCache.class).to(JiraItsServerCacheImpl.class);
        DynamicSet.bind(binder(), GitReferenceUpdatedListener.class)
            .to(JiraItsServerProjectCacheRefresher.class);
      }
    };
  }

  static class Loader extends CacheLoader<String, JiraItsServerInfo> {
    private final JiraConfig jiraConfig;

    @Inject
    Loader(JiraConfig jiraConfig) {
      this.jiraConfig = jiraConfig;
    }

    @Override
    public JiraItsServerInfo load(String projectName) {
      return jiraConfig.getServerInfoFor(projectName);
    }
  }
}
