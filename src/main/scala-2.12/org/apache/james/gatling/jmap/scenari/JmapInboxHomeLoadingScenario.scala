package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import org.apache.james.gatling.jmap.JmapMessages.openpaasListMessageParameters
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

  def generate(feederBuilder: FeederBuilder): ScenarioBuilder = {
    scenario("JmapHomeLoadingScenario")
      .feed(feederBuilder)
      .exec(CommonSteps.authentication())
      .group(InboxHomeLoading.name)(
        exec(RetryAuthentication.execWithRetryAuthentication(JmapMailbox.getMailboxes, JmapMailbox.getMailboxesChecks ++ JmapMailbox.saveInboxAs(Keys.inbox)))
          .exec(RetryAuthentication.execWithRetryAuthentication(JmapMessages.listMessages(openpaasListMessageParameters(Keys.inbox)), JmapMessages.listMessagesChecks))
          .exec(RetryAuthentication.execWithRetryAuthentication(JmapMessages.getMessages(JmapMessages.previewMessageProperties, Keys.messageIds), isSuccess)))
  }

}
