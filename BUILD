load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
)
load("//tools/bzl:genrule2.bzl", "genrule2")
load("//tools/bzl:js.bzl", "polygerrit_plugin")

gerrit_plugin(
    name = "its-jira",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: its-jira",
        "Gerrit-Module: com.googlesource.gerrit.plugins.its.jira.JiraModule",
        "Gerrit-InitStep: com.googlesource.gerrit.plugins.its.jira.InitJira",
        "Gerrit-ReloadMode: reload",
        "Implementation-Title: Jira ITS Plugin",
        "Implementation-URL: http://www.gerritforge.com",
    ],
    resource_jars = [":cs-its-jira-static"],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "//plugins/its-base",
    ],
)

genrule2(
    name = "cs-its-jira-static",
    srcs = [
        ":cs-its-jira-config",
    ],
    outs = ["cs-its-jira-static.jar"],
    cmd = " && ".join([
        "mkdir $$TMP/static",
        "cp -r $(locations :cs-its-jira-config) $$TMP/static",
        "cd $$TMP",
        "zip -Drq $$ROOT/$@ -g .",
    ]),
)

polygerrit_plugin(
    name = "cs-its-jira-config",
    srcs = glob([
        "cs-its-jira-config/*.html",
        "cs-its-jira-config/*.js",
    ]),
    app = "plugin-config.html",
)

junit_tests(
    name = "its_jira_tests",
    testonly = 1,
    srcs = glob(
        ["src/test/java/**/*.java"],
    ),
    tags = ["its-jira"],
    deps = [
        "its-jira__plugin_test_deps",
    ],
)

java_library(
    name = "its-jira__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":its-jira__plugin",
        "//plugins/its-base",
        "@mockito//jar",
        "@wiremock//jar",
    ],
)
