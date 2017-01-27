Build
=====

This @PLUGIN@ plugin is built with Buck.

Clone (or link) both this plugin and also
[plugins/its-base](https://gerrit-review.googlesource.com/#/admin/projects/plugins/its-base)
to the `plugins` directory of Gerrit's source tree.

Then issue

```
  cd plugins/@PLUGIN@

  cp -f external_plugin_deps.bzl ../

  cd ../../

  bazel build plugins/@PLUGIN@
```

in the root of Gerrit's source tree to build

The output is created in

```
  bazel-genfiles/plugins/@PLUGIN@/@PLUGIN@.jar
```

This project can be imported into the Eclipse IDE:

```
  cd plugins/@PLUGIN@

  cp -f external_plugin_deps.bzl ../

  cd ../../

  ./tools/eclipse/project.py
```

To execute the tests run:

```
  bazel test plugins/@PLUGIN@
```

[Back to @PLUGIN@ documentation index][index]

[index]: index.html