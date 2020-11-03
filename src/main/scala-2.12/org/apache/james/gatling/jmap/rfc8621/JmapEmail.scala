package org.apache.james.gatling.jmap.rfc8621

import io.gatling.core.Predef.{StringBody, jsonPath, _}
import io.gatling.core.json.Json
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.jmap.draft.JmapMessages.NO_PARAMETERS

object JmapEmail {
  type JmapParameters = Map[String, Any]

  def queryEmails(queryParameters: JmapParameters = NO_PARAMETERS): HttpRequestBuilder = {
    val params = queryParameters + ("accountId" -> "${accountId}")
    JmapHttp.apiCall("emailQuery")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail"],
           |  "methodCalls": [[
           |    "Email/query",
           |    ${Json.stringify(params)},
           |    "c1"]]
           |}""".stripMargin))
  }

  val nonEmptyListMessagesChecks: HttpCheck =
    jsonPath("$.methodResponses[0][1].ids[*]").findAll.saveAs("emailIds")

  val typicalMessageProperties: List[String] = List("bcc", "cc", "date", "from", "hasAttachment", "htmlBody", "id", "isAnswered", "isDraft", "isFlagged", "isUnread", "mailboxIds", "size", "subject", "textBody", "to")

  def getRandomEmails(properties: List[String] = typicalMessageProperties, emailIdsKey: String = "emailIds",
                      accountId: String = "accountId"): HttpRequestBuilder = {
    JmapHttp.apiCall("emailGet")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail"],
           |  "methodCalls": [[
           |    "Email/get",
           |    {
           |      "accountId": "$${$accountId}",
           |      "ids": ["$${$emailIdsKey.random()}"],
           |      "properties": ${Json.stringify(properties)}
           |    },
           |    "c1"]]
           |}""".stripMargin))
  }
}
