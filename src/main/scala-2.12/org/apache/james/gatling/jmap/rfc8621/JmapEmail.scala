package org.apache.james.gatling.jmap.rfc8621

import io.gatling.core.Predef.{StringBody, jsonPath, _}
import io.gatling.core.check.extractor.jsonpath.{JsonPathCheckBuilder, JsonPathOfType}
import io.gatling.core.json.Json
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.jmap.draft.JmapMessages.NO_PARAMETERS

case class RequestTitle(title: String) extends AnyVal
case class KeywordName(name: String) extends AnyVal

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

  def openpaasEmailQueryParameters(mailboxKey: String = "inboxID"): JmapParameters =
    Map("filter" -> Map("inMailbox" -> s"$${$mailboxKey}"),
      "comparator" -> List(Map("property" -> "sentAt", "isAscending" -> false)),
      "position" -> 0,
      "limit" -> 30)

  def nonEmptyListMessagesChecks(key: String = "emailIds"): HttpCheck =
    jsonPath("$.methodResponses[0][1].ids[*]").findAll.saveAs(key)

  private val emailsPath: JsonPathCheckBuilder[String] with JsonPathOfType = jsonPath("$.methodResponses[0][1].list[*]")
  val nonEmptyEmailsChecks: HttpCheck = emailsPath.exists

  val typicalMessageProperties: List[String] = List("bcc", "cc", "receivedAt", "sentAt", "from", "hasAttachment", "htmlBody", "id", "keywords", "mailboxIds", "size", "subject", "textBody", "to")
  val previewMessageProperties: List[String] = List("bcc", "cc", "receivedAt", "sentAt", "from", "hasAttachment", "id", "keywords", "mailboxIds", "size", "subject", "to", "preview")

  def getRandomEmails(properties: List[String] = typicalMessageProperties,
                      emailIdsKey: String = "emailIds",
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

  def getEmails(properties: List[String] = typicalMessageProperties,
                emailIdsKey: String = "emailIds",
                accountId: String = "accountId"): HttpRequestBuilder = {
    JmapHttp.apiCall("emailGet")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail"],
           |  "methodCalls": [[
           |    "Email/get",
           |    {
           |      "accountId": "$${$accountId}",
           |      "ids": $${$emailIdsKey.jsonStringify()},
           |      "properties": ${Json.stringify(properties)}
           |    },
           |    "c1"]]
           |}""".stripMargin))
  }

  def markAsSeen(emailIdsKey: String = "emailIds",
                 accountId: String = "accountId"): HttpRequestBuilder =
    performUpdate(RequestTitle("markAsSeen"), KeywordName("$seen"),
      emailIdsKey = emailIdsKey, accountId = accountId)

  def markAsAnswered(emailIdsKey: String = "emailIds",
                     accountId: String = "accountId"): HttpRequestBuilder =
    performUpdate(RequestTitle("markAsAnswered"), KeywordName("$answered"),
      emailIdsKey = emailIdsKey, accountId = accountId)

  def markAsFlagged(emailIdsKey: String = "emailIds",
                    accountId: String = "accountId"): HttpRequestBuilder =
    performUpdate(RequestTitle("markAsFlagged"), KeywordName("$flagged"),
      emailIdsKey = emailIdsKey, accountId = accountId)

  def performUpdate(title: RequestTitle,
                    keywordName: KeywordName,
                    emailIdsKey: String = "emailIds",
                    accountId: String = "accountId"): HttpRequestBuilder = {
    JmapHttp.apiCall(title.title)
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail"],
           |  "methodCalls": [[
           |    "Email/set",
           |    {
           |      "accountId": "$${$accountId}",
           |      "update": {
           |        "$${$emailIdsKey.random()}": {
           |          "$keywordName": true
           |        }
           |      }
           |    },
           |    "c1"]]
           |}""".stripMargin))
  }
}
