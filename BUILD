load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
    "PLUGIN_DEPS",
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
        "Implementation-URL: https://ezratin@gerrit.ericsson.se/a/gerrit/plugins/its-jira",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "@its-base//jar",
    ],
)

java_library(
    name = "its-jira__plugin_classpath_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + [
        ":its-jira__plugin",
        "@its-base//jar",
    ],
)
