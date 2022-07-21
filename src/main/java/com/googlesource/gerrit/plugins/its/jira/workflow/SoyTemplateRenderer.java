// Copyright (C) 2022 The Android Open Source Project
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

import com.google.common.io.CharStreams;
import com.google.inject.ProvisionException;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce.Renderer;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoyTemplateRenderer {

  private static final Logger log = LoggerFactory.getLogger(SoyTemplateRenderer.class);

  private final Path templateDir;

  public SoyTemplateRenderer(Path itsPath) {
    this.templateDir = itsPath.resolve("templates");
  }

  private String soyTextTemplate(
      SoyFileSet.Builder builder, String template, Map<String, String> properties) {

    Path templatePath = templateDir.resolve(template + ".soy");
    String content;

    try (Reader r = Files.newBufferedReader(templatePath, StandardCharsets.UTF_8)) {
      content = CharStreams.toString(r);
    } catch (IOException err) {
      throw new ProvisionException(
          "Failed to read template file " + templatePath.toAbsolutePath().toString(), err);
    }

    builder.add(content, templatePath.toAbsolutePath().toString());
    Renderer renderer =
        builder
            .build()
            .compileTemplates()
            .renderTemplate("etc.its.templates." + template)
            .setData(properties);
    String rendered = renderer.renderText().get();
    log.debug("Rendered template {} to:\n{}", templatePath, rendered);
    return rendered;
  }

  public String render(String template, Map<String, String> properties) throws IOException {
    return soyTextTemplate(SoyFileSet.builder(), template, properties);
  }
}
