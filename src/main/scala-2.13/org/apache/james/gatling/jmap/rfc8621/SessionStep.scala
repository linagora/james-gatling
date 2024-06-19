package org.apache.james.gatling.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object SessionStep {
  def retrieveAccountId: ChainBuilder =
    exec(
      http("retrieveAccountId")
        .get("/jmap/session")
        .headers(JmapHttp.HEADERS_JSON)
        .basicAuth("#{username}", "#{password}")
        .check(status.is(200))
        .check(jsonPath("$.primaryAccounts.urn:ietf:params:jmap:mail").find.saveAs("accountId")))
}
