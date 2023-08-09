package org.apache.james.gatling.jmap.rfc8621

import com.fasterxml.jackson.databind.JsonNode
import io.gatling.core.Predef.{StringBody, jsonPath, _}
import io.gatling.core.check.MultipleFindCheckBuilder
import io.gatling.core.check.jsonpath.{JsonPathCheckType, JsonPathOfType}
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.jmap.draft.JmapMessages.{JmapParameters, NO_PARAMETERS}
import org.apache.james.gatling.jmap.draft.{JmapMessages, MessageId, Subject, TextBody}
import org.apache.james.gatling.jmap.rfc8621.scenari.PushPlatformValidationScenario.accountId
import org.apache.james.gatling.utils.RandomStringGenerator

case class RequestTitle(title: String) extends AnyVal
case class KeywordName(name: String) extends AnyVal

object JmapEmail {
  def queryEmails(queryParameters: JmapParameters = NO_PARAMETERS): HttpRequestBuilder = {
    JmapHttp.apiCall("emailQuery")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail", "urn:apache:james:params:jmap:mail:shares"],
           |  "methodCalls": [[
           |    "Email/query",
           |    {
           |      "accountId": "$${$accountId}"
           |      $queryParameters
           |    },
           |    "c1"]]
           |}""".stripMargin))
  }

  def openpaasEmailQueryParameters(mailboxKey: String = "inboxID"): JmapParameters = {
    s""",
       |"filter": {
       |  "inMailbox": "$${$mailboxKey}"
       |},
       |"sort": [{
       |  "property": "sentAt",
       |  "isAscending": false
       |}],
       |"position": 0,
       |"limit": 30
       |""".stripMargin
  }

  def nonEmptyListMessagesChecks(key: String = "emailIds"): HttpCheck =
    jsonPath("$.methodResponses[0][1].ids[*]").findAll.saveAs(key)

  def emailCreatedChecks(key: String = "emailCreated"): HttpCheck =
    jsonPath("$.methodResponses[0][1].created").find.saveAs(key)

  def emailSubmittedChecks(key: String = "emailSubmitted"): HttpCheck =
    jsonPath("$.methodResponses[1][1].created").find.saveAs(key)

  private val emailsPath: MultipleFindCheckBuilder[JsonPathCheckType, JsonNode, String] with JsonPathOfType = jsonPath("$.methodResponses[0][1].list[*]")
  private val statePath = "$.methodResponses[0][1].state"
  private val newStatePath = "$.methodResponses[0][1].newState"

  val nonEmptyEmailsChecks: HttpCheck = emailsPath.exists

  val typicalMessageProperties: String = "[\"bcc\", \"cc\", \"receivedAt\", \"sentAt\", \"from\", \"hasAttachment\", \"htmlBody\", \"id\", \"keywords\", \"mailboxIds\", \"size\", \"subject\", \"textBody\", \"to\"]"
  val previewMessageProperties: String = "[\"bcc\", \"cc\", \"receivedAt\", \"sentAt\", \"from\", \"hasAttachment\", \"id\", \"keywords\", \"mailboxIds\", \"size\", \"subject\", \"to\", \"preview\"]"

  def getRandomEmails(properties: String = typicalMessageProperties,
                      emailIdsKey: String = "emailIds",
                      accountId: String = "accountId"): HttpRequestBuilder = {
    JmapHttp.apiCall("emailGet")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail", "urn:apache:james:params:jmap:mail:shares"],
           |  "methodCalls": [[
           |    "Email/get",
           |    {
           |      "accountId": "$${$accountId}",
           |      "ids": ["$${$emailIdsKey.random()}"],
           |      "properties": $properties
           |    },
           |    "c1"]]
           |}""".stripMargin))
  }

  def getEmails(properties: String = typicalMessageProperties,
                emailIdsKey: String = "emailIds",
                accountId: String = "accountId"): HttpRequestBuilder = {
    JmapHttp.apiCall("emailGet")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail", "urn:apache:james:params:jmap:mail:shares"],
           |  "methodCalls": [[
           |    "Email/get",
           |    {
           |      "accountId": "$${$accountId}",
           |      "ids": $${$emailIdsKey.jsonStringify()},
           |      "properties": $properties
           |    },
           |    "c1"]]
           |}""".stripMargin))
  }

  def getState(accountId: String = "accountId"): HttpRequestBuilder = {
    JmapHttp.apiCall("emailGetState")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail", "urn:apache:james:params:jmap:mail:shares"],
           |  "methodCalls": [[
           |    "Email/get",
           |    {
           |      "accountId": "$${$accountId}",
           |      "ids": []
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
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail", "urn:apache:james:params:jmap:mail:shares"],
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

  def submitEmails(recipientFeeder: RecipientFeederBuilder): ChainBuilder = {
    val mailFeeder = Iterator.continually(
      Map(JmapMessages.messageIdSessionParam -> MessageId().id,
        JmapMessages.subjectSessionParam -> Subject().subject,
        JmapMessages.textBodySessionParam -> TextBody().text))

    feed(mailFeeder)
      .feed(recipientFeeder)
      .exec(submitEmail()
        .check(JmapHttp.statusOk, JmapHttp.noError, JmapEmail.emailCreatedChecks(), JmapEmail.emailSubmittedChecks()))
  }

  def submitEmail(title: RequestTitle = RequestTitle("submitEmails"),
                  accountId: String = "accountId",
                  username: String = "username",
                  mailboxId: String = "draftMailboxId",
                  messageId: String = "messageId",
                  recipient: String = "recipient",
                  subject: String = "subject",
                  textBody: String = "textBody"): HttpRequestBuilder =
    JmapHttp.apiCall(title.title)
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail", "urn:ietf:params:jmap:submission", "urn:apache:james:params:jmap:mail:shares"],
           |  "methodCalls": [
           |    ["Email/set", {
           |      "accountId": "$${$accountId}",
           |      "create": {
           |        "$${$messageId}": {
           |          "mailboxIds": {
           |            "$${$mailboxId}": true
           |          },
           |          "subject": "$${$subject}",
           |          "from": [{"email": "$${$username}"}],
           |          "to": [{"email": "$${$recipient}"}],
           |          "htmlBody": [
           |            {
           |              "partId": "1",
           |              "type": "text/html"
           |            }
           |          ],
           |          "bodyValues": {
           |            "1": {
           |              "value": "$${$textBody}"
           |            }
           |          }
           |        }
           |      }
           |    }, "c1"],
           |    ["EmailSubmission/set", {
           |      "accountId": "$${$accountId}",
           |      "create": {
           |        "$SubmissionId": {
           |          "emailId": "#$${$messageId}",
           |          "envelope": {
           |            "mailFrom": {"email": "$${$username}"},
           |            "rcptTo": [{"email": "$${$recipient}"}]
           |          }
           |        }
           |      }
           |    }, "c1"]
           |  ]
           |}""".stripMargin
      ))

  def saveStateAs(key: String): HttpCheck = jsonPath(statePath).saveAs(key)

  def saveNewStateAs(key: String): HttpCheck = jsonPath(newStatePath).saveAs(key)

  def getNewState(accountId: String = "accountId", oldState: String = "oldState"): HttpRequestBuilder = {
    JmapHttp.apiCall("emailChanges")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core", "urn:ietf:params:jmap:mail", "urn:apache:james:params:jmap:mail:shares"],
           |  "methodCalls": [
           |    ["Email/changes", {
           |      "accountId": "$${$accountId}",
           |      "sinceState": "$${$oldState}"
           |    }, "c1"],
           |    ["Email/get", {
           |      "accountId": "$${$accountId}",
           |      "#ids": {
           |        "resultOf": "c1",
           |        "name": "Email/changes",
           |        "path": "/created"
           |      },
           |      "properties":[ "id", "blobId", "threadId", "mailboxIds", "keywords", "size",
           |                     "sender", "from", "to", "cc", "bcc", "replyTo", "subject",
           |                     "sentAt", "hasAttachment", "preview"]
           |    }, "c2"],
           |    ["Email/get", {
           |      "accountId": "$${$accountId}",
           |      "#ids": {
           |        "resultOf": "c1",
           |        "name": "Email/changes",
           |        "path": "/updated"
           |      },
           |      "properties":["keywords", "mailboxIds"]
           |    }, "c3"]
           |  ]
           |}""".stripMargin))
      .check(status.is(200))
      .check(jsonPath("$.methodResponses[0][1].newState").saveAs(oldState))
  }
}

case class SubmissionId(id: String = RandomStringGenerator.randomString) extends AnyVal