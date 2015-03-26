Build
=====

This @PLUGIN@ plugin is built with Buck.

Clone (or link) both this plugin and also
[plugins/its-base](https://gerrit-review.googlesource.com/#/admin/projects/plugins/its-base)
to the `plugins` directory of Gerrit's source tree.

Then issue

```
  buck build plugins/@PLUGIN@
```

in the root of Gerrit's source tree to build

The output is created in

```
  buck-out/gen/plugins/@PLUGIN@/@PLUGIN@.jar
```

This project can be imported into the Eclipse IDE:

```
  ./tools/eclipse/project.py
```

To execute the tests run:

```
  buck test --all --include @PLUGIN@
```

[Back to @PLUGIN@ documentation index][index]

[index]: index.html