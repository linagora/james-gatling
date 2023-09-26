package org.apache.james.gatling.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder

import scala.concurrent.duration._

object JmapMailbox {
  private val loopVariableName = "any"

  def getMailboxes: HttpRequestBuilder =
    JmapHttp.apiCall("getMailboxes")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail", "urn:apache:james:params:jmap:mail:shares"],
           |  "methodCalls": [[
           |    "Mailbox/get",
           |    {
           |      "accountId": "$${accountId}",
           |      "ids": null
           |    },
           |    "c1"]]
           |}""".stripMargin))

  private val mailboxListPath = "$.methodResponses[0][1].list"
  val inboxIdPath = s"$mailboxListPath[?(@.role == 'inbox')].id"
  private val outboxIdPath = s"$mailboxListPath[?(@.role == 'outbox')].id"
  private val sentIdPath = s"$mailboxListPath[?(@.role == 'sent')].id"
  private val draftsIdPath = s"$mailboxListPath[?(@.role == 'drafts')].id"
  private val trashIdPath = s"$mailboxListPath[?(@.role == 'trash')].id"
  private val spamIdPath = s"$mailboxListPath[?(@.role == 'spam')].id"
  private val statePath = "$.methodResponses[0][1].state"
  private val newStatePath = "$.methodResponses[0][1].newState"

  val getSystemMailboxesChecks: Seq[HttpCheck] = Seq[HttpCheck](JmapHttp.statusOk, JmapHttp.noError) ++
    Seq[HttpCheck](
      jsonPath(inboxIdPath).saveAs("inboxMailboxId"),
      jsonPath(outboxIdPath).saveAs("outboxMailboxId"),
      jsonPath(sentIdPath).saveAs("sentMailboxId"),
      jsonPath(draftsIdPath).saveAs("draftMailboxId"),
      jsonPath(trashIdPath).saveAs("trashMailboxId"),
      jsonPath(spamIdPath).saveAs("spamMailboxId"))

  private def mailboxesIdPathForMailboxesWithAtLeastMessages(nbMessages : Int) = s"$mailboxListPath[?(@.totalEmails >= $nbMessages)].id"

  def saveInboxAs(key: String): HttpCheck = jsonPath(inboxIdPath).saveAs(key)

  def saveDraftAs(key: String): HttpCheck = jsonPath(draftsIdPath).saveAs(key)

  def saveSpamAs(key: String): HttpCheck = jsonPath(spamIdPath).saveAs(key)

  def saveOutboxAs(key: String): HttpCheck = jsonPath(outboxIdPath).saveAs(key)

  def saveRandomMailboxWithAtLeastMessagesAs(key: String, atLeastMessages : Int): HttpCheck =
    jsonPath(mailboxesIdPathForMailboxesWithAtLeastMessages(atLeastMessages))
      .findRandom
      .saveAs(key)

  def provisionSystemMailboxes(): ChainBuilder =
    exec(JmapMailbox.getMailboxes
      .check(getSystemMailboxesChecks: _*))
      .pause(1 second)

  def provisionUsersWithMessages(recipientFeeder: RecipientFeederBuilder, numberOfMessages: Int): ChainBuilder =
    exec(provisionSystemMailboxes())
      .repeat(numberOfMessages, loopVariableName) {
        exec(JmapEmail.submitEmails(recipientFeeder))
          .pause(1 second, 2 seconds)
      }
      .pause(5 second)

  def saveStateAs(key: String): HttpCheck = jsonPath(statePath).saveAs(key)

  def saveNewStateAs(key: String): HttpCheck = jsonPath(newStatePath).saveAs(key)

  def getNewState(accountId: String = "accountId", oldState: String = "oldState"): HttpRequestBuilder = {
    JmapHttp.apiCall("mailboxChanges")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core", "urn:ietf:params:jmap:mail", "urn:apache:james:params:jmap:mail:shares"],
           |  "methodCalls": [
           |    ["Mailbox/changes", {
           |      "accountId": "$${$accountId}",
           |      "sinceState": "$${$oldState}"
           |    }, "c1"],
           |    ["Mailbox/get", {
           |      "accountId": "$${$accountId}",
           |      "#ids": {
           |        "resultOf": "c1",
           |        "name": "Mailbox/changes",
           |        "path": "/created"
           |      }
           |    }, "c2"],
           |    ["Mailbox/get", {
           |      "accountId": "$${$accountId}",
           |      "#ids": {
           |        "resultOf": "c1",
           |        "name": "Mailbox/changes",
           |        "path": "/updated"
           |      }
           |    }, "c3"]
           |  ]
           |}""".stripMargin))
      .check(status.is(200))
      .check(jsonPath("$.methodResponses[0][1].newState").saveAs(oldState))
  }
}
