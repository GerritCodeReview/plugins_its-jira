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
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.its.jira;

import static com.google.common.truth.Truth.assertThat;

import com.google.gerrit.extensions.api.projects.ConfigValue;
import org.junit.Test;

public class JiraPasswordProjectConfigEntryTest {

  JiraPasswordProjectConfigEntry entry = new JiraPasswordProjectConfigEntry("JIRA password");
  ConfigValue configValue = new ConfigValue();

  @Test
  public void testModifyConfigValue() {
    configValue.value = "password";
    ConfigValue modValue = entry.preUpdate(configValue);
    assertThat(modValue.value).isEqualTo("password");
  }

  @Test
  public void testEmptyValue() {
    configValue.value = "";
    ConfigValue modValue = entry.preUpdate(configValue);
    assertThat(modValue).isEqualTo(configValue);
  }

  @Test
  public void testNullValue() {
    configValue.value = null;
    ConfigValue modValue = entry.preUpdate(configValue);
    assertThat(modValue.value).isNull();
  }
}
