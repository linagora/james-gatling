package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.JmapMessages.openpaasListMessageParameters
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapMailbox, JmapMessages, RetryAuthentication}

class JmapMessageFlagUpdatesScenario {
  private object Keys {
    val inbox = "inboxID"
    val messageIds = "messageIds"
    val messagesDetailList = "messagesDetailList"
  }

  def generate(userFeeder: UserFeederBuilder): ScenarioBuilder = {
    scenario("JmapMessageFlagUpdatesScenario")
      .feed(userFeeder)
      .exec(CommonSteps.authentication())
      .group(MessageFlagUpdates.name)(
        exec(RetryAuthentication.execWithRetryAuthentication(JmapMailbox.getMailboxes, JmapMailbox.getMailboxesChecks ++ JmapMailbox.saveInboxAs(Keys.inbox)))
          .exec(RetryAuthentication.execWithRetryAuthentication(JmapMessages.listMessages(openpaasListMessageParameters(Keys.inbox)), JmapMessages.nonEmptyListMessagesChecks))
          .randomSwitch(
            70.0 -> exec(JmapMessages.markAsRead()),
            20.0 -> exec(JmapMessages.markAsAnswered()),
            10.0 -> exec(JmapMessages.markAsFlagged())
          ))
  }
}
