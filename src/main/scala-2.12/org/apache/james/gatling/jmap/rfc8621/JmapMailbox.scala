package org.apache.james.gatling.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.http

object JmapMailbox {
  def getMailboxes: ChainBuilder =
    exec(
      http("getMailboxes")
        .post("/jmap")
        .headers(JmapHttp.HEADERS_JSON)
        .basicAuth("${username}", "${password}")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail"],
           |  "methodCalls": [[
           |    "Mailbox/get",
           |    {
           |      "accountId": "$${accountId}",
           |      "ids": null
           |    },
           |    "c1"]]
           |}""".stripMargin)))
}
