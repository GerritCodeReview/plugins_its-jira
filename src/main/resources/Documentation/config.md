Plugin @PLUGIN@
===============

This plugin allows to associate Jira issues to Git commits.

It can be configured per project whether the Jira integration is
enabled or not. To enable the Jira integration for a project the
project must have the following entry in its `project.config` file in
the `refs/meta/config` branch:

```
  [plugin "its-jira"]
    enabled = true
```

If `plugin.its-jira.enabled` is not specified in the `project.config` file
the value is inherited from the parent project. If it is also not set
on any parent project the Jira integration is disabled for this
project.

By setting `plugin.its-jira.enabled` to true in the `project.config` of the
`All-Projects` project the Jira integration can be enabled by default
for all projects. During the initialization of the plugin you are asked
if the Jira integration should be enabled by default for all projects
and if yes this setting in the `project.config` of the `All-Projects`
project is done automatically.

If child projects must not be allowed to disable the Jira integration
a project can enforce the Jira integration for all child projects by
setting `plugin.its-jira.enabled` to `enforced`.

On the project info screen there is a dropdown list for the
`plugin.its-jira.enabled` parameter which offers the values `true`,
`false`, `enforced` and `INHERIT`. Project owners can change this
parameter and save it. If the Jira integration is enforced by a parent
project the dropdown list is disabled.

The Jira integration can be limited to specific branches by setting
`plugin.its-jira.branch`. The branches may be configured using explicit
branch names, ref patterns, or regular expressions. Multiple branches
may be specified.

E.g. to limit the Jira integration to the `master` branch and all
stable branches the following could be configured:

```
  [plugin "its-jira"]
    enabled = true
    branch = refs/heads/master
    branch = ^refs/heads/stable-.*
```

Comment links
----------------

Git commits are associated to Jira issues reusing the existing Gerrit
[commitLink configuration][1] to extract the issue-id from the commit
messages.

[1]: ../../../Documentation/config-gerrit.html#__a_id_commentlink_a_section_commentlink

Additionally you need to specify the enforcement policy for git commits
with regards to issue-tracker associations; the following values are supported:

MANDATORY
:	 One or more issue-ids are required in the git commit message, otherwise
	 the git push will be rejected.

SUGGESTED
:	 Whenever a git commit message does not contain any issue-id,
	 a warning message is displayed as a suggestion on the client.

OPTIONAL
:	 Issue-ids are linked when found in a git commit message. No warning is
	 displayed otherwise.

Example:

    [commentLink "its-jira"]
    match = ([A-Z]+-[0-9]+)
    html = "<a href=\"http://jira.example.com/browse/$1\">$1</a>"
    association = SUGGESTED

Jira connectivity
-----------------

In order for Gerrit to connect to Jira/SOAP-API URL and credentials
are required in your `gerrit.config` / `secure.config` under the
`[its-jira]` section.

Example:

    [its-jira]
    url=http://jira.example.com
    username=admin
    password=jirapass

Jira credentials and connectivity details are asked and verified during the Gerrit init.

This plugin also offers an option to configure specific JIRA instances and can be
configured using ITS-JIRA plugin's
display section in Gerrit UI. It provides options to input an url, username and password.
The credentials mentioned on project level will take precedence over
the global credentials mentioned in gerrit.config.

Gerrit init integration
-----------------------

The Jira plugin comes with a Gerrit init step that simplifies the
initial configuration. It guides through the configuration of the Jira
integration and checks the connectivity.

Gerrit init example:

    *** Jira Integration
    ***

    Issue tracker integration for all projects? [DISABLED/?]: enabled
    Branches for which the issue tracker integration should be enabled (ref, ref pattern or regular expression) [refs/heads/*]:

    *** Jira connectivity
    ***

    Jira URL (empty to skip)       [http://jira.example.com]:
    Jira username                  [admin]:
    Change admin's password        [y/N]? y
    admin's password               : *****
                  confirm password : *****
    Test connectivity to http://jira.example.com [y/N]: y
    Checking Jira connectivity ... [OK]

    *** Jira issue-tracking association
    ***

    Jira issue-Id regex            [([A-Z]+-[0-9]+)]:
    Issue-id enforced in commit message [MANDATORY/?]: ?
           Supported options are:
           mandatory
           suggested
           optional
    Issue-id enforced in commit message [MANDATORY/?]: suggested

The connectivity of its-jira plugin with Jira server happens on-request i.e.
when an action is requested, a connection is established based on any of the two
configuration's availability i.e. global config extracted from gerrit.config or
project level config from project.config.

The way a Jira issue and its corresponding gerrit change are
annotated can be configured by specifying rules in a separate config file. Global rules,
applied by all configured ITS plugins, can be defined in the file
`review_site/etc/its/actions.config`. Rules specific to @PLUGIN@ are defined in
the file `review_site/etc/its/actions-@PLUGIN@.config`.

**Sample actions-@Plugin@.config:**

    [rule "open"]
        event-type = patchset-created
        action = add-velocity-comment inline Change ${its.formatLink($changeUrl)} is created.
        action = In Progress
    [rule "resolve"]
        event-type = comment-added
        approval-Code-Review = 2
        action = add-velocity-comment inline Change ${its.formatLink($changeUrl)} is verified.
        action = In Review
    [rule "merged"]
        event-type = change-merged
        action = add-velocity-comment inline Change ${its.formatLink($changeUrl)} is merged.
        action = Done
    [rule "abandoned"]
        event-type = change-abandoned'
        action = add-velocity-comment inline Change ${its.formatLink($changeUrl)} is abandoned.
        action = To Do

The first rule triggers an action which adds a comment and a hyperlink to the change created
in gerrit. The comment will appear in an Jira issue's `Comment` section whenever a patchset-created event
is triggered. The second action item in the first rule transitions the state of the issue
in Jira to `In Progress`. The title of the action `In Progress` should match the workflow actions
used by the JIRA server as different versions of JIRA can have different workflow actions.

**Note:** Velocity comments were deprecated in Gerrit 2.14 and will be removed in Gerrit 2.16/3.0;
the `actions-@Plugin@.config` needs to be changed accordingly. For example, to use Soy comments instead
of velocity comments:

    [rule "open"]
        event-type = patchset-created
        action = add-soy-comment Change ${its.formatLink($changeUrl)} is created.
        action = In Progress
