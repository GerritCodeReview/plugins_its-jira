load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
)

gerrit_plugin(
    name = "its-jira",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    manifest_entries = [
        "Gerrit-PluginName: its-jira",
        "Gerrit-Module: com.googlesource.gerrit.plugins.its.jira.JiraModule",
        "Gerrit-InitStep: com.googlesource.gerrit.plugins.its.jira.InitJira",
        "Gerrit-ReloadMode: reload",
        "Implementation-Title: Jira ITS Plugin",
        "Implementation-URL: http://www.gerritforge.com",
        "Implementation-Vendor: GerritForge LLP",
    ],
    deps = [
        "//plugins/its-base",
        "//plugins/its-jira/lib:jira-rest-java-client-core",
        "//plugins/its-jira/lib:atlassian-httpclient-apache-httpcomponents",
        "//plugins/its-jira/lib:jira-rest-java-client-api",
        "//plugins/its-jira/lib:atlassian-util-concurrent"
    ],
)

