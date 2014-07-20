Build
=====

This plugin is built with Buck.

Two build modes are supported: Standalone and in Gerrit tree. Standalone
build mode is recommended, as this mode doesn't require local Gerrit
tree to exist.

Build standalone
----------------

Prerequisites: build and install its-base library.

Clone bucklets library:

```
  git clone https://gerrit.googlesource.com/bucklets

```
and link it to its-jira directory:

```
  cd its-jira && ln -s ../bucklets .
```

Add link to the .buckversion file:

```
  cd its-jira && ln -s bucklets/buckversion .buckversion
```

To build the plugin, issue the following command:

```
  buck build plugin
```

The output is created in

```
  buck-out/gen/its-jira/its-jira.jar
```

Build in Gerrit tree
--------------------

Clone or link this plugin to the plugins directory of Gerrit's source
tree, and issue the command:

```
  buck build plugins/its-jira
```

The output is created in

```
  buck-out/gen/plugins/its-jira/its-jira.jar
```

This project can be imported into the Eclipse IDE:

```
  ./tools/eclipse/project.py
```

Note that for compatibility reasons a Maven build is provided, but is
considered to be deprecated and will be removed in a future version of
this plugin.

To build with Maven, change directory to the plugin folder and issue the
command:

```
  mvn clean package
```

When building with Maven, the Gerrit Plugin API must be available.

How to build the Gerrit Plugin API is described in the [Gerrit
documentation](../../../Documentation/dev-buck.html#_extension_and_plugin_api_jar_files).
