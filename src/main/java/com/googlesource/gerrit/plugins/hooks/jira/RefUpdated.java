// Copyright (C) 2012 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.hooks.jira;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.events.GitReferenceUpdatedListener;
import com.google.gerrit.reviewdb.client.Project.NameKey;
import com.google.gerrit.server.git.LocalDiskRepositoryManager;
import com.google.inject.Inject;

@Listen
class RefUpdated implements GitReferenceUpdatedListener {
  private static final Logger log = LoggerFactory
      .getLogger(GitReferenceUpdatedListener.class);

  private final JiraClient jiraRpc;
  private final File gitDir;
  private final LocalDiskRepositoryManager repositoryManager;
  private Pattern commentPattern;
  private Pattern issuePattern = Pattern.compile("[A-Z][A-Z]+-[1-9][0-9]*");
  private String gitwebUrl;

  @Inject
  RefUpdated(final JiraClient jiraRpc, JiraPluginConfig config,
      LocalDiskRepositoryManager repositoryManager) {
    this.jiraRpc = jiraRpc;
    this.gitDir = config.gitBasePath;
    this.commentPattern = Pattern.compile(config.issueRegex);
    this.gitwebUrl = config.gitwebUrl;
    this.repositoryManager = repositoryManager;
  }

  public void onGitReferenceUpdated(Event event) {
    Repository repo;
    try {
      repo =
          repositoryManager.openRepository(new NameKey(event.getProjectName()));
    } catch (IOException e) {
      log.error("Cannot open Gerrit Project " + event.getProjectName(), e);
      return;
    }

    try {
      RevWalk revWalk = new RevWalk(repo);
      JiraClientSession jira;
      try {
        jira = jiraRpc.newSession();
      } catch (XmlRpcException e) {
        log.error("Cannot open a new session to Jira", e);
        return;
      }

      try {
        for (Update u : event.getUpdates()) {
          String newObjId = u.getRefName();
          if (newObjId == null) {
            continue;
          }

          RevCommit commit;
          try {
            commit = revWalk.parseCommit(ObjectId.fromString(newObjId));
          } catch (Exception e) {
            log.error("Unable to parse commit object " + newObjId
                + ": skipping to next update event", e);
            continue;
          }

          process(jira, event.getProjectName(), u.getRefName(), commit);
        }
      } finally {
        jira.close();
      }
    } finally {
      repo.close();
    }
  }

  private void process(JiraClientSession jira, String projectName,
      String refName, RevCommit commit) {
    String commitMsg = commit.getFullMessage();
    Matcher matcher = commentPattern.matcher(commitMsg);
    while (matcher.find()) {
      String matched = matcher.group();
      try {
        addComment(jira, matched, projectName, refName, commit);
      } catch (XmlRpcException e) {
        log.error("Unable to add new comment to Jira issue " + matched, e);
      }
    }
  }

  private void addComment(JiraClientSession jira, String commentMatch,
      String projectName, String refName, RevCommit commit)
      throws XmlRpcException {
    Matcher matcher = issuePattern.matcher(commentMatch);
    if (!matcher.find()) {
      return;
    }

    jira.addComment(matcher.group(), getComment(projectName, refName, commit));
  }

  private String getComment(String projectName, String refName, RevCommit commit) {
    String commitId = commit.getName();
    String comment =
        String.format("Git commit: %s\n" + "Branch: %s\n" + "Author: %s\n"
            + "Committer: %s\n" + "%s", commitId, refName,
            getIdentity(commit.getAuthorIdent()),
            getIdentity(commit.getCommitterIdent()), commit.getFullMessage());
    if (gitwebUrl != null) {
      comment =
          String.format("%s\n%s?p=%s.git;a=commit;h=%s", comment, gitwebUrl,
              projectName, commitId);
    }
    return comment;
  }

  private String getIdentity(PersonIdent ident) {
    return String.format("%s <%s>", ident.getName(), ident.getEmailAddress());
  }
}
