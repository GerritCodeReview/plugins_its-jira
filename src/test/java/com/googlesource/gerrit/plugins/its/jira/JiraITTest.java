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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gerrit.acceptance.GerritConfig;
import com.google.gerrit.acceptance.LightweightPluginDaemonTest;
import com.google.gerrit.acceptance.NoHttpd;
import com.google.gerrit.acceptance.TestPlugin;
import com.google.gerrit.acceptance.UseLocalDisk;
import com.google.gerrit.testing.ConfigSuite;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

@RunWith(ConfigSuite.class)
@UseLocalDisk
@NoHttpd
@TestPlugin(name = "its-jira", sysModule = "com.googlesource.gerrit.plugins.its.jira.JiraModule")
public class JiraITTest extends LightweightPluginDaemonTest {
  private static final String PLUGIN_NAME = "its-jira";
  private static final String BASE_PREFIX = "/rest/api/2";
  private static final String SERVER_INFO_PREFIX = "/serverinfo";
  private static final String COMMENT_CLASS_PREFIX = "/comment";
  private static final String TRANSITION = "transition";
  private static final String TRANSITION_RESPONSE_BODY =
      format("{\"transitions\":[{\"name\":\"%s\",\"id\":\"1\"}]}", TRANSITION);
  private static final String ISSUE_CLASS_PREFIX = "/issue/";
  private static final String TRANSITIONS_CLASS_PREFIX = "/transitions";
  private static final String JIRA_ISSUE = "JIRA-1000";
  private static final int PORT = 19888;
  private static final String URL = "http://localhost:" + PORT;
  private static final String COMMENT_SECTION = "commentLink." + PLUGIN_NAME;

  private Path its_dir;

  @Rule public WireMockRule wireMockRule = new WireMockRule(PORT);

  @Override
  public void beforeTest(Description description) throws Exception {
    super.beforeTest(description);
    createItsDir();
    createItsRulesConfig();
  }

  @Before
  public void enablePluginInProjectConfig() throws Exception {
    projectCache
        .checkedGet(project)
        .getConfig()
        .getPluginConfig(PLUGIN_NAME)
        .setString("enabled", "true");
  }

  @Test
  @GerritConfig(name = COMMENT_SECTION + ".match", value = "([A-Z]+-[0-9]+)")
  @GerritConfig(
    name = COMMENT_SECTION + ".html",
    value = "<a href=\\\"http://localhost:19888/browse/$1\\\">$1</a>"
  )
  @GerritConfig(name = COMMENT_SECTION + ".association", value = "SUGGESTED")
  @GerritConfig(name = PLUGIN_NAME + ".url", value = URL)
  @GerritConfig(name = PLUGIN_NAME + ".username", value = "admin")
  @GerritConfig(name = PLUGIN_NAME + ".password", value = "admin")
  public void test() throws Exception {
    pushCommitWithIssueExists();
    pushCommitWithIssueNotExists();
    pushCommitWithIssueForbidden();
  }

  private void pushCommitWithIssueExists() throws Exception {
    mockServerCall();
    mockJiraActionCalls();
    wireMockRule.givenThat(
        WireMock.get(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE)).willReturn(ok()));

    createChangeWithIssue();

    verifyCommonCalls();
    wireMockRule.verify(
        getRequestedFor(
            urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE + TRANSITIONS_CLASS_PREFIX)));
    wireMockRule.verify(
        postRequestedFor(
            urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE + TRANSITIONS_CLASS_PREFIX)));
    wireMockRule.verify(
        postRequestedFor(
            urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE + COMMENT_CLASS_PREFIX)));
  }

  private void pushCommitWithIssueNotExists() throws Exception {
    mockServerCall();
    wireMockRule.givenThat(
        WireMock.get(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_NOT_FOUND)));
    createChangeWithIssue();
    verifyCommonCalls();
  }

  private void pushCommitWithIssueForbidden() throws Exception {
    mockServerCall();
    wireMockRule.givenThat(
        WireMock.get(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE))
            .willReturn(aResponse().withStatus(HTTP_FORBIDDEN)));
    createChangeWithIssue();
    verifyCommonCalls();
  }

  private void mockServerCall() {
    wireMockRule.resetRequests();
    wireMockRule.givenThat(
        WireMock.get(urlEqualTo(BASE_PREFIX + SERVER_INFO_PREFIX)).willReturn(ok()));
  }

  private void mockJiraActionCalls() {
    wireMockRule.givenThat(
        WireMock.get(
                urlEqualTo(
                    BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE + TRANSITIONS_CLASS_PREFIX))
            .willReturn(okJson(TRANSITION_RESPONSE_BODY)));
    wireMockRule.givenThat(
        WireMock.post(
                urlEqualTo(
                    BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE + TRANSITIONS_CLASS_PREFIX))
            .willReturn(noContent()));
    wireMockRule.givenThat(
        WireMock.post(
                urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE + COMMENT_CLASS_PREFIX))
            .willReturn(aResponse().withStatus(HTTP_CREATED)));
  }

  private void verifyCommonCalls() {
    wireMockRule.verify(getRequestedFor(urlEqualTo(BASE_PREFIX + ISSUE_CLASS_PREFIX + JIRA_ISSUE)));
  }

  private void createChangeWithIssue() throws Exception {
    pushFactory
        .create(db, admin.getIdent(), testRepo, JIRA_ISSUE, "a.txt", "test")
        .to("refs/for/master");
  }

  private void createItsDir() throws IOException {
    its_dir = server.getSitePath().resolve("etc").resolve("its");
    Files.createDirectories(its_dir);
  }

  private void createItsRulesConfig() throws IOException {
    FileBasedConfig cfg =
        new FileBasedConfig(its_dir.resolve("actions-its-jira.config").toFile(), FS.DETECTED);
    cfg.setString("rule", "open", "event-type", "patchset-created");
    List<String> actions = new ArrayList<>();
    actions.add(TRANSITION);
    actions.add("add-comment Change created");
    cfg.setStringList("rule", "open", "action", actions);
    cfg.save();
  }
}
