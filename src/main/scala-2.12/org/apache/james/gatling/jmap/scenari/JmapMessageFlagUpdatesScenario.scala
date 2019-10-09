package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

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
        randomSwitch(
          70.0 -> exec(JmapMessages.markAsRead()),
          20.0 -> exec(JmapMessages.markAsAnswered()),
          10.0 -> exec(JmapMessages.markAsFlagged())
        ))
  }
}
