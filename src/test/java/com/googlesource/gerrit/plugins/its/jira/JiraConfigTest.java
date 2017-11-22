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

// <<<<<<< HEAD
// import com.googlesource.gerrit.plugins.its.jira.restapi.JiraURL;
// import java.net.MalformedURLException;
// =======
// >>>>>>> e21d533... Support multiple Jira instances
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JiraConfigTest {

  // <<<<<<< HEAD
  //  private static final String PASS = "pass";
  //  private static final JiraURL TEST_URL = newUrl("http://jira_example.com/");
  //  private static final String USER = "user";
  //  private static final String PLUGIN_NAME = "its-jira";
  //
  // =======
  // >>>>>>> e21d533... Support multiple Jira instances
  //  @Rule public ExpectedException thrown = ExpectedException.none();
  //  @Mock private Config cfg;
  //  @Mock private PluginConfigFactory cfgFactory;
  //
  //  private String pluginName = "its-jira";
  //  private JiraConfig jiraConfig;
  //
  //  @Before
  //  public void createJiraConfig() {
  //    jiraConfig = new JiraConfig(cfg, pluginName, cfgFactory);
  //  }
  //
  //  @Test
  //// <<<<<<< HEAD
  ////  public void gerritConfigContainsNullValues() throws Exception {
  ////    thrown.expect(RuntimeException.class);
  ////    jiraConfig = new JiraConfig(cfg, PLUGIN_NAME);
  ////  }
  ////
  ////  private static JiraURL newUrl(String url) {
  ////    try {
  ////      return new JiraURL(url);
  ////    } catch (MalformedURLException e) {
  ////      throw new RuntimeException(e);
  ////    }
  //// =======
  //  public void testGetPluginConfigFor() throws NoSuchProjectException {
  //    Project.NameKey project = new Project.NameKey("$project");
  //    PluginConfig pluginCfg = new PluginConfig(pluginName, new Config());
  //    when(cfgFactory.getFromProjectConfigWithInheritance(project,
  // pluginName)).thenReturn(pluginCfg);
  //    jiraConfig.getPluginConfigFor(project);
  //    assertThat(pluginCfg).isNotNull();
  //// >>>>>>> e21d533... Support multiple Jira instances
  //  }
}
