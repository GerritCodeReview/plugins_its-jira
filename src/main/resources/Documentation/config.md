Plugin @PLUGIN@
===============

This plugin allows to associate Jira issues to Git commits.

It can be configured per project whether the Jira integration is
enabled or not. To enable the Jira integration for a project the
project must have the following entry in its `project.config` file in
the `refs/meta/config` branch:

```ini
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

```ini
  [plugin "its-jira"]
    enabled = true
    branch = refs/heads/master
    branch = ^refs/heads/stable-.*
```

Comment links
-------------

Git commits are associated to Jira issues reusing the existing Gerrit
[commentLink configuration][1] to extract the issue-id from the commit
messages.

[1]: ../../../Documentation/config-gerrit.html#commentlink

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

```ini
    [commentLink "its-jira"]
    match = ([A-Z]+-[0-9]+)
    html = "<a href=\"http://jira.example.com/browse/$1\">$1</a>"
    association = SUGGESTED
```

Jira connectivity
-----------------

In order for Gerrit to connect to Jira/SOAP-API URL and credentials
are required in your `gerrit.config` / `secure.config` under the
`[its-jira]` section.

Example:

```ini
    [its-jira]
    url=http://jira.example.com
    username=admin
    password=jirapass
```

Jira credentials and connectivity details are asked and verified during the Gerrit init.

Timeouts
--------

The HTTP connection and read timeouts are configurable in every place where the Jira URL is specified,
either in the `gerrit.config` or `project.config`, whichever has the precedence.

`connectTimeout`
:	Timeout period for making a new HTTP connection to the Jira URL.
	The value is in the usual time-unit format like "1 s", "100 ms", etc.
	By default `2 minutes`.

`readTimeout`
:	The read timeout for the HTTP operation to complete.
	The value is in the usual time-unit format like "1 s", "100 ms", etc.
	A timeout can be used to avoid blocking all of the incoming Git receive-packs threads in case
	Jira server becomes slow.
	By default `30 seconds`.

Gerrit init integration
-----------------------

The Jira plugin comes with a Gerrit init step that simplifies the
initial configuration. It guides through the configuration of the Jira
integration and checks the connectivity.

Gerrit init example:

```text
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
```

The connectivity of its-jira plugin with Jira server happens on-request. When an
action is requested, a connection is established based on any of the two
configuration's availability, i.e., global config extracted from gerrit.config or
project level config from project.config.

The way a Jira issue and its corresponding gerrit change are annotated can be
configured by specifying rules in a separate config file. Global rules, applied
by all configured ITS plugins, can be defined in the file
`review_site/etc/its/actions.config`. Rules specific to @PLUGIN@ are defined in
the file `review_site/etc/its/actions-@PLUGIN@.config`.

**Sample actions-@PLUGIN@.config:**

```ini
    [rule "open"]
        event-type = patchset-created
        action = add-standard-comment
        action = In Progress
    [rule "resolve"]
        event-type = comment-added
        approvalCodeReview = 2
        action = add-standard-comment
        action = In Review
    [rule "merged"]
        event-type = change-merged
        action = add-standard-comment
        action = Done
    [rule "abandoned"]
        event-type = change-abandoned
        action = add-standard-comment
        action = To Do
```

The first rule triggers an action which adds a comment and a hyperlink to the change created
in gerrit. The comment will appear in an Jira issue's `Comment` section whenever a patchset-created
event is triggered. The second action item in the first rule transitions the state of the issue
in Jira to `In Progress`. The title of the action `In Progress` should match the workflow actions
used by the JIRA server as different versions of JIRA can have different workflow actions.

**Note:** Velocity comments were deprecated in Gerrit 2.14 and will be removed in Gerrit 2.16/3.0;
the `actions-@PLUGIN@.config` needs to be changed accordingly. For example, to use Soy comments
instead of velocity comments:

```ini
    [rule "open"]
        event-type = patchset-created
        action = add-soy-comment Change ${its.formatLink($changeUrl)} is created.
        action = In Progress
```

Multiple Jira servers integration
---------------------------------

> Please note that this feature is considered EXPERIMENTAL and should be used with
> caution, as it could expose sensitive information.

In corporate environments, it is not unusual to have multiple Jira servers
and it is a common requirement to integrate Gerrit projects with those.

This plugin offers the possibility of configuring integrations with multiple Jira
servers at the Gerrit project level, i.e., a Gerrit project can be associated with
a particular Jira instance. This is done by specifying the Jira server URL,
username and password in the project configuration using the GUI controls
this plugin adds to the project's General page. In this case, the *commentlink*
section is automatically added by the plugin. It is also possible to add the
configuration entries by manually editing the *project.config* file in the
*refs/meta/config* branch.

A typical Jira server configuration in the *project.config* file will look like:

```ini
    [plugin "its-jira"]
         enabled = true
         instanceUrl = http://jiraserver:8075/
         jiraUsername = *user*
         password = *pass*

    [commentlink "its-jira"]
         match = ([A-Z]+-[0-9]+)
         link = http://jiraserver:8075/browse/$1
```

A different project could define its own Jira server in its *project.config*
file:

```ini
    [plugin "its-jira"]
         enabled = true
         instanceUrl = http://other_jiraserver:7171/
         jiraUsername = *another_user*
         password = *another_pass*

    [commentlink "its-jira"]
         match = (JIRA-ISSUE:[0-9]+)
         link = http://other_jiraserver:7171/browse/$1
```

In case its-jira plugin is enabled for a project but no Jira server is configured
for the project, i.e., it is not specified in the *project.config* file, the
default configuration will be the one defined in *gerrit.config*.

If no Jira server information is defined in *gerrit.config* either, an error is
logged and the Jira integration is disabled for the project.

The credentials mentioned at the project level, i.e., in the *project.config* file,
will take precedence over the global credentials mentioned in *secure.config*.
It is important to notice that __the credentials at the project level are stored as
clear text and will be visible to anyone having access to the
*refs/meta/config branch* like project owners and site administrators__. This is a
limitation and the reason why this feature is marked as experimental, i.e., not
production ready. Additional work is needed in order to offer a secure level of
encryption for this information.

Specific actions
----------------

### invoke-issue-restapi

The `invoke-issue-restapi` invokes RestAPI on the issue, it uses soy template to
generate the request.

```ini
  action = invoke-issue-restapi method uri passCodes template
```

For example if you would like to create a link in issues to review, use the
following action:

```ini
  action = invoke-issue-restapi POST /remotelink 200,201 link
```

With the follwing `its/templates/link.soy` template:

```
{namespace etc.its.templates}
{template link}
  {@param changeUrl: string}
  {@param subject: string}
  {@param status: string}
{lb}
  "globalId": "{$changeUrl}",
  "application": {lb}
    "type": "com.googlesource.gerrit",
    "name": "Gerrit"
  {rb},
  "object": {lb}
    "url": "{$changeUrl}",
    "title": "{$subject}",
    "icon": {lb}
      "url16x16": "https://www.gerritcodereview.com/images/diffy_logo.png",
      "title": "Review"
    {rb},
    "status": {lb}
      {switch $status}
        {case null}
          "resolved": false
        {case 'NEW'}
          "resolved": false
        {case 'SUBMITTED'}
          "resolved": false
        {case 'MERGED'}
          "resolved": true
        {case 'ABANDONED'}
          "resolved": true
      {/switch}
    {rb}
  {rb}
{rb}
{/template}
```

### invoke-project-restapi

The `invoke-project-restapi` is similar to `invoke-issue-restapi`, it invokes
project method instead of issue method.

### mark-property-as-released-version

The `mark-property-as-released-version` action marks a version as released in
JIRA.
The version to mark as released is identified by an event property value.

This is useful when you want to mark a version as released in JIRA when a
tag is created in the Gerrit project.

Example with the event property `ref`:

```ini
  action = mark-property-as-released-version ref
```

### add-comment

The `add-comment`, `add-standard-comment` or `add-soy-comment` actions add comment for
certain events. By default, these comments are visible to all on JIRA instance.

It is possible to get better control over comments visibility by adding configuration
entries in *gerrit.config* or *project.config* file in the *refs/meta/config* branch.

A typical visibility configuration will look like:

```
  [plugin "its-jira"]
    visibilityType = role
    visibilityValue = Dev
```

This will publish comments visible only by users with *role* set as *Dev* in JIRA.

`visibilityType` and `visibilityValue` must be set together.

`visibilityType` could be set to **role** or **group**, and `visibilityValue` refers
to JIRA *role* or *group*.
