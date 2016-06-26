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
    match = (\\[[A-Z][A-Z]+-[1-9][0-9]*\\])
    html = "<a href=\"http://jira.example.com/browse/$1\">$1</a>"
    association = SUGGESTED

Once a Git commit with a comment link is detected, the Jira issue ID
is extracted and a new comment is added to the issue, pointing back to
the original Git commit.

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

GitWeb integration
----------------

When Gerrit gitweb is configured, an additional direct link from Jira to GitWeb
will be created, pointing exactly to the Git commit ID containing the Jira issue ID.

Issues workflow automation
--------------------------

Jira plugin is able to automate status transition on the issues based on
code-review actions performed on Gerrit; actions are performed on Jira using
the username/password provided during Gerrit init.
Transition automation is driven by `$GERRIT_SITE/etc/its/actions-jira.config`
file.

The syntax of the actions-jira.config follows the conventions described in its-base
config-rulebase.common.md

Example:

[rule "open"]
    event-type = patchset-created
    action = add-standard-comment
    action = Start Progress
[rule "resolve"]
    verified = +1
    approval-Code-Review = +2
    action = Resolve Issue
[rule "merged"]
    event-type = change-merged
    action = Close Issue
[rule "abandoned"]
    event-type = change-abandoned
    action = Stop progress


The above example defines four status transitions on Jira, based on the following conditions:

* Whenever a new pathset is created on Gerrit, start progress on the Jira issue
* Whenever a change is verified and reviewed with +2, set the Jira issue to resolved
* Whenever a change is merged to the branch, close the Jira issue
* Whenever a change is abandoned, stop the progress on the Jira issue
