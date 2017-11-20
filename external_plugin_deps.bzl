load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
  maven_jar(
    name = "its-base",
    artifact = "com.googlesource.gerrit.plugins:its-base:d532183",
    repository = "https://arm.mo.ca.am.ericsson.se/artifactory/proj-gerrit-release-local",
  )

