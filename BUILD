load("//tools/bzl:plugin.bzl", "gerrit_plugin")

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
    ],
)

