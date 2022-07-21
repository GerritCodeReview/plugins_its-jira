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

class InvokeRestAPIParametersExtractor {

  private static final Logger log = LoggerFactory.getLogger(InvokeRestAPIParametersExtractor.class);

  @Inject
  public InvokeRestAPIParametersExtractor() {}

  public Optional<InvokeRestAPIParameters> extract(
      ActionRequest actionRequest, Map<String, String> properties) {
    String[] parameters = actionRequest.getParameters();
    if (parameters.length != 4) {
      log.error(
          "Wrong number of received parameters. Received parameters are {}. Three parameters are"
              + " expected, method, uri, passCodes and template.",
          Arrays.toString(parameters));
      return Optional.empty();
    }

    String method = parameters[0];
    if (Strings.isNullOrEmpty(method)) {
      log.error("Received property id is blank");
      return Optional.empty();
    }
    String uri = parameters[1];
    if (Strings.isNullOrEmpty(uri)) {
      log.error("Received property uri is blank");
      return Optional.empty();
    }
    String passCodesStr = parameters[2];
    if (Strings.isNullOrEmpty(passCodesStr)) {
      log.error("Received property passCodes is blank");
      return Optional.empty();
    }
    int[] passCodes = Arrays.stream(passCodesStr.split(",")).mapToInt(Integer::parseInt).toArray();
    String template = parameters[3];
    if (Strings.isNullOrEmpty(template)) {
      log.error("Received property template is blank");
      return Optional.empty();
    }

    return Optional.of(new InvokeRestAPIParameters(method, uri, passCodes, template));
  }
}
