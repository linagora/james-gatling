package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.CommonSteps.authentication
import org.apache.james.gatling.jmap.draft.JmapMailbox.{getSystemMailboxes, getSystemMailboxesChecks}
import org.apache.james.gatling.jmap.draft.JmapMessages.openpaasListMessageParameters
import org.apache.james.gatling.jmap.draft.{JmapMailbox, JmapMessages, RetryAuthentication}
import org.apache.james.gatling.jmap.{InboxHomeLoading, OpenMessage, SelectMailbox}

import scala.concurrent.duration._

class PlatformValidationScenario(minMessagesInMailbox: Int, minWaitDelay: Duration = 20 seconds, maxWaitDelay: Duration = 40 seconds) {
  private object Keys {
    val randomMailbox = "randomMailbox"
    val inbox = "inboxID"
    val messageIds = "messageIds"
    val messagesDetailList = "messagesDetailList"
  }

  val inboxHomeLoading: JmapInboxHomeLoadingScenario = new JmapInboxHomeLoadingScenario
  val openArbitrary: JmapOpenArbitraryMessageScenario = new JmapOpenArbitraryMessageScenario
  val selectArbitrary: JmapSelectArbitraryMailboxScenario = new JmapSelectArbitraryMailboxScenario(minMessagesInMailbox)
  def sendMessage(recipientFeeder: RecipientFeederBuilder): ChainBuilder = JmapMessages.sendMessagesToUserWithRetryAuthentication(recipientFeeder)
  val flagUpdate: ChainBuilder = randomSwitch(
    70.0 -> exec(JmapMessages.markAsRead()),
    20.0 -> exec(JmapMessages.markAsAnswered()),
    10.0 -> exec(JmapMessages.markAsFlagged()))

  def prepare: ChainBuilder =
    exec(authentication())
      .pause(1 second)
      .exec(RetryAuthentication.execWithRetryAuthentication(getSystemMailboxes(),
        getSystemMailboxesChecks
          ++ JmapMailbox.saveRandomMailboxWithAtLeastMessagesAs(Keys.randomMailbox, minMessagesInMailbox)
          ++ JmapMailbox.saveInboxAs(Keys.inbox)))
      .pause(1 second)
      .exec(RetryAuthentication.execWithRetryAuthentication(JmapMessages.listMessages(openpaasListMessageParameters(Keys.inbox)), JmapMessages.nonEmptyListMessagesChecks))

  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("JmapSendMessages")
      .feed(userFeeder)

      .group("prepare")(
        // Prepare everything that your simulation needs
        exec(prepare))

      // What does a user do when using JMAP?
      .during(duration) {
        randomSwitch(
            // Full reload every 10 minutes (=> mailbox polling)
            5.0 -> group(InboxHomeLoading.name)(inboxHomeLoading.inboxHomeLoading),
            // Refresh the message list every 1 minute (=> message polling)
            50.0 -> group(SelectMailbox.name)(selectArbitrary.selectArbitrary),

            // Send an email every 10 minutes
            5.0 -> group("sendMessage")(sendMessage(recipientFeeder)),
            // Open an email every minutes
            30.0 -> group(OpenMessage.name)(openArbitrary.openArbitrary),
            10.0 -> group("updateFlags")(flagUpdate))
          // a user interacts every 30s in average
          .pause(minWaitDelay, maxWaitDelay)
      }
}
