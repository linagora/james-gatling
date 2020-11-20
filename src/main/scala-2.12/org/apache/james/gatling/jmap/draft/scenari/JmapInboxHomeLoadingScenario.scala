package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.InboxHomeLoading
import org.apache.james.gatling.jmap.draft.JmapMessages.openpaasListMessageParameters
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapChecks, JmapMailbox, JmapMessages, RetryAuthentication}

class JmapInboxHomeLoadingScenario {

  private object Keys {
    val inbox = "inboxID"
    val messageIds = "messageIds"
    val messagesDetailList = "messagesDetailList"
  }

  private val isSuccess: Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError)

  def generate(userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("JmapHomeLoadingScenario")
      .feed(userFeeder)
      .exec(CommonSteps.authentication())
      .group(InboxHomeLoading.name)(inboxHomeLoading)

  def inboxHomeLoading: ChainBuilder =
      exec(RetryAuthentication.execWithRetryAuthentication(JmapMailbox.getMailboxes, JmapMailbox.getMailboxesChecks ++ JmapMailbox.saveInboxAs(Keys.inbox)))
        .exec(RetryAuthentication.execWithRetryAuthentication(JmapMessages.listMessages(openpaasListMessageParameters(Keys.inbox)), JmapMessages.nonEmptyListMessagesChecks))
        .exec(RetryAuthentication.execWithRetryAuthentication(JmapMessages.getMessages(JmapMessages.previewMessageProperties, Keys.messageIds), isSuccess))
}
