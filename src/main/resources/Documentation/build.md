Build
=====

This @PLUGIN@ plugin is built with Bazel.

Clone (or link) both this plugin and also
[plugins/its-base](https://gerrit-review.googlesource.com/#/admin/projects/plugins/its-base)
to the `plugins` directory of Gerrit's source tree.

Put the external dependency Bazel build file into the Gerrit plugins directory,
replacing the existing empty one.

```
  cd gerrit/plugins
  ln -sf @PLUGIN@/external_plugin_deps.bzl .
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
Add the plugin name to the `CUSTOM_PLUGINS` and
`CUSTOM_PLUGINS_TEST_DEPS` sets in the file
`<gerrit_source_code>/tools/bzl/plugins.bzl` and execute:

```
  ./tools/eclipse/project.py
```

To execute the tests run:

```
  bazel test plugins/@PLUGIN@:its_jira_tests
```

[Back to @PLUGIN@ documentation index][index]

[index]: index.html