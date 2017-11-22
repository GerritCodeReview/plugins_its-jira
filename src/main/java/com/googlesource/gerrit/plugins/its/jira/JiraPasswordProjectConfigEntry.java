// Copyright (C) 2018 Open Source Project
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

import com.google.gerrit.extensions.api.projects.ConfigValue;
import com.google.gerrit.server.config.ProjectConfigEntry;

/** A {@link ProjectConfigEntry} for the Jira password. */
class JiraPasswordProjectConfigEntry extends ProjectConfigEntry {

  /** Builds a @{link ProjectConfigEntry}. */
  JiraPasswordProjectConfigEntry(String displayName) {
    super(displayName, "");
  }

  /**
   * Take the input value and encrypt it to save in the project config.
   *
   * @param configValue the original value
   * @return encrypted text
   */
  @Override
  public ConfigValue preUpdate(ConfigValue configValue) {
    if (configValue.value != null && !configValue.value.isEmpty()) {
      configValue.value = encrypt(configValue.value);
    }
    return configValue;
  }

  /**
   * Encrypt a string.
   *
   * <p>For now, this method is returning the same parameter it receives as the encryption code was
   * deemed unsuitable to be added to this same plugin.
   *
   * <p>Instead, work to add encrypting capabilities to Gerrit core is in process [1]; once this is
   * completed, this method should be modified to integrate the final encryption implementation.
   *
   * <p>[1] https://gerrit-review.googlesource.com/c/gerrit/+/177390
   *
   * @param value the String to encrypt
   * @return encrypted text
   */
  private String encrypt(String value) {
    return value;
  }
}
