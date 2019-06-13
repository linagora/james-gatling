package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.jmap.{CommonSteps, JmapMailbox, JmapMessages}

import scala.concurrent.duration._

class JmapAllScenario {

  def generate(feeder: FeederBuilder, duration: Duration, recipientFeeder: FeederBuilder): ScenarioBuilder =
    scenario("JmapAllScenarios")
      .feed(feeder)
      .exec(CommonSteps.provisionSystemMailboxes())
      .during(duration) {
        feed(recipientFeeder)
        .exec(JmapMessages.sendMessagesToUserWithRetryAuthentication())
        .pause(1 second, 5 seconds)
        .exec(JmapMailbox.getSystemMailboxesWithRetryAuthentication)
        .exec(JmapMessages.listMessagesWithRetryAuthentication())
        .exec(JmapMessages.getMessagesWithRetryAuthentication())
        .pause(1 second, 5 seconds)
        .randomSwitch(
          70.0 -> exec(JmapMessages.markAsRead()),
          20.0 -> exec(JmapMessages.markAsAnswered()),
          10.0 -> exec(JmapMessages.markAsFlagged())
        )
    }

}
