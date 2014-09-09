def genwsdl2java(
    name,
    srcs,
    out):
  genrule(
    name = name,
    srcs = srcs,
      cmd = '$(exe :wsdl2java) -o $TMP ' +
      '-p com.atlassian.jira.rpc.soap.client $SRCS && ' +
      'cd $TMP && ' +
      'zip -qr $OUT .',
    out = out,
  )

gerrit_plugin(
  name = 'its-jira',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: its-jira',
    'Gerrit-Module: com.googlesource.gerrit.plugins.hooks.jira.JiraModule',
    'Gerrit-InitStep: com.googlesource.gerrit.plugins.hooks.jira.InitJira',
    'Gerrit-ReloadMode: reload',
    'Implementation-Title: Plugin its-jira',
    'Implementation-URL: http://www.gerritforge.com',
    'Implementation-Vendor: GerritForge LLP',
  ],
  deps = [
    ':wsdl2java_lib',
    '//plugins/its-base:its-base__plugin',
    '//plugins/its-jira/lib/axis:axis',
    '//plugins/its-jira/lib:xmlrpc-client',
    '//plugins/its-jira/lib:wsdl4j',
  ],
)

java_library(
  name = 'wsdl2java_lib',
  srcs = [':wsdl2java_src'],
  deps = ['//plugins/its-jira/lib/axis:axis'],
)

genwsdl2java(
  name = 'wsdl2java_src',
  srcs = ['src/main/wsdl/jirasoapservice-v2.wsdl'],
  out = 'wsdl2java.src.zip',
)

java_binary(
  name = 'wsdl2java',
  main_class = 'org.apache.axis.wsdl.WSDL2Java',
  deps = [
    '//plugins/its-jira/lib/axis:axis',
    '//plugins/its-jira/lib/axis:wsdl4j',
  ],
)
