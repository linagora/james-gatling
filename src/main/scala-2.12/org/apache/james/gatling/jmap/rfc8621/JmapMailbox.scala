package org.apache.james.gatling.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder

object JmapMailbox {
  def getMailboxes: HttpRequestBuilder =
      JmapHttp.apiCall("getMailboxes")
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
           |}""".stripMargin))

  private val mailboxListPath = "$.methodResponses[0][1].list"
  private val inboxIdPath = s"$mailboxListPath[?(@.role == 'inbox')].id"

  def saveInboxAs(key: String): HttpCheck = jsonPath(inboxIdPath).saveAs(key)
}
