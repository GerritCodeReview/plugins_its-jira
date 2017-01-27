Build
=====

This @PLUGIN@ plugin is built with Bazel.

You need to clone atlassian-webhooks-plugin and jira-rest-java-client from GitHub
and build it locally using the install MAVEN target.

```
  $ git clone https://github.com/paladox/atlassian-webhooks-plugin.git
  $ cd atlassian-webhooks-plugin
  $ mvn install
  $ cd ../
  $ git clone https://github.com/paladox/jira-rest-java-client.git
  $ cd jira-rest-java-client
  $ mvn install
  $ cd ../
```

Clone (or link) both this plugin and also
[plugins/its-base](https://gerrit-review.googlesource.com/#/admin/projects/plugins/its-base)
to the `plugins` directory of Gerrit's source tree.

Put the external dependency Bazel build file into the Gerrit /plugins directory,
replacing the existing empty one.

```
  cd gerrit/plugins
  rm external_plugin_deps.bzl
  ln -s @PLUGIN@/external_plugin_deps.bzl .
```

Then issue

```
  bazel build plugins/@PLUGIN@
```

in the root of Gerrit's source tree to build

The output is created in

```
  bazel-genfiles/plugins/@PLUGIN@/@PLUGIN@.jar
```

This project can be imported into the Eclipse IDE.
Add the plugin name to the `CUSTOM_PLUGINS` set in
Gerrit core in `tools/bzl/plugins.bzl`, and execute:

```
  ./tools/eclipse/project.py
```

To execute the tests run:

```
  bazel test plugins/@PLUGIN@
```

[Back to @PLUGIN@ documentation index][index]

[index]: index.html