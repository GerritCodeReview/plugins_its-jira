load("//tools/bzl:maven_jar.bzl", "maven_jar", "MAVEN_LOCAL")

ATLASSIAN_REPO = 'https://maven.atlassian.com/repository/public/'

def external_plugin_deps():
  maven_jar(
    name = 'jira_rest_java_client_core',
    artifact = 'com.atlassian.jira:jira-rest-java-client-core:3.0.0',
    deps = [
    ],
    repository = ATLASSIAN_REPO,
  )

