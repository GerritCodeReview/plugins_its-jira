package com.googlesource.gerrit.plugins.its.jira.restapi;

import java.util.List;

/**
 * Created on 03/06/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class JiraVersionsPage extends JiraPage<JiraVersion> {
  public JiraVersionsPage(
      String self,
      String nextPage,
      int maxResults,
      int startAt,
      int total,
      boolean isLast,
      List<JiraVersion> values) {
    super(self, nextPage, maxResults, startAt, total, isLast, values);
  }

  public JiraVersion findByName(String name) {
    return this.getValues().stream().filter(v -> name.equals(v.getName())).findFirst().orElse(null);
  }
}
