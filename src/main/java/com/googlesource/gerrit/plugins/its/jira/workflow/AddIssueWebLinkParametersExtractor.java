// Copyright (C) 2018 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.its.jira.workflow;

import com.google.common.base.Strings;
import com.googlesource.gerrit.plugins.its.base.workflow.ActionRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AddIssueWebLinkParametersExtractor {

  private static final Logger log =
      LoggerFactory.getLogger(AddIssueWebLinkParametersExtractor.class);

  @Inject
  public AddIssueWebLinkParametersExtractor() {}

  public Optional<AddIssueWebLinkParameters> extract(
      ActionRequest actionRequest, Map<String, String> properties) {
    String[] parameters = actionRequest.getParameters();
    if (parameters.length != 3) {
      log.error(
          "Wrong number of received parameters. Received parameters are {}. Three parameters are"
              + " expected, globalId, url and title.",
          Arrays.toString(parameters));
      return Optional.empty();
    }

    String id = parameters[0];
    if (Strings.isNullOrEmpty(id)) {
      log.error("Received property id is blank");
      return Optional.empty();
    }
    if (!properties.containsKey(id)) {
      log.error("Resolved property id is blank");
      return Optional.empty();
    }

    String url = parameters[1];
    if (Strings.isNullOrEmpty(url)) {
      log.error("Received property url is blank");
      return Optional.empty();
    }
    if (!properties.containsKey(url)) {
      log.error("Resolved property url is blank");
      return Optional.empty();
    }

    String title = parameters[2];
    if (Strings.isNullOrEmpty(title)) {
      log.error("Received property title is blank");
      return Optional.empty();
    }
    if (!properties.containsKey(title)) {
      log.error("Resolved property title is blank");
      return Optional.empty();
    }

    return Optional.of(
        new AddIssueWebLinkParameters(
            properties.get(id), properties.get(url), properties.get(title)));
  }
}
