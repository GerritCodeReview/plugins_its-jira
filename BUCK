gerrit_plugin(
  name = 'its-jira',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-Module: com.googlesource.gerrit.plugins.its.jira.JiraModule',
    'Gerrit-InitStep: com.googlesource.gerrit.plugins.its.jira.InitJira',
    'Gerrit-ReloadMode: reload',
    'Implementation-Title: Jira ITS Plugin',
    'Implementation-URL: http://www.gerritforge.com',
    'Implementation-Vendor: GerritForge LLP',
  ],
  deps = [
    '//plugins/its-base:its-base__plugin',
    '//lib/commons:codec',
  ],
)
