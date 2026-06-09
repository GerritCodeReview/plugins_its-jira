Build
=====

This @PLUGIN@ plugin is built with Bazel.

Clone (or link) both this plugin and also
[plugins/its-base](https://gerrit-review.googlesource.com/#/admin/projects/plugins/its-base)
to the `plugins` directory of Gerrit's source tree.

Wire the plugin's Maven dependencies into the in-tree build by linking its
module fragment into the Gerrit plugins directory.

```
  cd gerrit/plugins
  ln -sf @PLUGIN@/external_plugin_deps.MODULE.bazel .
```

Then issue

```
  bazel build plugins/@PLUGIN@
```

in the root of Gerrit's source tree to build

The output is created in

```
  bazel-bin/plugins/@PLUGIN@/@PLUGIN@.jar
```

To execute the tests run:

```
  bazel test plugins/@PLUGIN@:its_jira_tests
```

[Back to @PLUGIN@ documentation index][index]

[index]: index.html