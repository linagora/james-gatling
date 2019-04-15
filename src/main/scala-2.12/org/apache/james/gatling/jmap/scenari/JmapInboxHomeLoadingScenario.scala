package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.jmap._

class JmapInboxHomeLoadingScenario {

  private object Keys {
    val inbox = "inboxID"
    val messageIds = "messageIds"
    val messagesDetailList = "messagesDetailList"
  }

  private val isSuccess: Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError)

  private val mailboxListPath = "[0][1].list"

  private val inboxIdPath =s"$$$mailboxListPath[?(@.name == 'INBOX')].id"

  private val listAllMailboxesAndSelectInbox: HttpRequestBuilder =
    JmapAuthentication.authenticatedQuery("getMailboxes", "/jmap")
      .body(StringBody("""[["getMailboxes",{},"#0"]]"""))
      .check(jsonPath(inboxIdPath).find.saveAs(Keys.inbox))

  val listMessagesInInbox: HttpRequestBuilder =
    JmapAuthentication.authenticatedQuery("listMessagesInInbox", "/jmap")
      .body(StringBody(
        """[["getMessageList",
          |{"filter":{"inMailboxes":["${inboxID}"],
          |"text":null},
          |"sort":["date desc"],
          |"collapseThreads":false,
          |"fetchMessages":false,
          |"position":0,
          |"limit":30},"#0"]]""".stripMargin))
      .check(jsonPath("$[0][1].messageIds[*]").findAll.saveAs(Keys.messageIds))


  private val getMessages: HttpRequestBuilder = JmapAuthentication.authenticatedQuery("getMessages", "/jmap")
    .body(StringBody(
      """[["getMessages",
        |{"properties": ["id","blobId","threadId","headers","subject","from","to","cc","bcc","replyTo","preview","date","isUnread",
        |"isFlagged",
        |"isDraft",
        |"hasAttachment",
        |"mailboxIds",
        |"isAnswered",
        |"isForwarded"
        |]
        |,"ids": ${messageIds.jsonStringify()}},
        |"#0"]]""".stripMargin))


  def generate(feederBuilder: FeederBuilder): ScenarioBuilder = {
    scenario("JmapHomeLoadingScenario")
      .feed(feederBuilder)
      .exec(CommonSteps.authentication())
      .group(InboxHomeLoading.name)(
        exec(RetryAuthentication.execWithRetryAuthentication(listAllMailboxesAndSelectInbox, JmapMailbox.getMailboxesChecks))
          .exec(RetryAuthentication.execWithRetryAuthentication(listMessagesInInbox, JmapMessages.listMessagesChecks))
          .exec(RetryAuthentication.execWithRetryAuthentication(getMessages, isSuccess)))

  }

}
