load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
)

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
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "//plugins/its-base",
        "@jasypt//jar",
    ],
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
        "@jasypt//jar",
    ],
)
