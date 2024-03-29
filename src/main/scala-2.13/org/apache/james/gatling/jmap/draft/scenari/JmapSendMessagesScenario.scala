package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapMessages}

import scala.concurrent.duration._

class JmapSendMessagesScenario {

  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("JmapSendMessages")
      .feed(userFeeder)
      .exec(CommonSteps.provisionSystemMailboxes())
      .during(duration.toSeconds.toInt) {
        exec(JmapMessages.sendMessagesToUserWithRetryAuthentication(recipientFeeder))
          .pause(1 second , 2 seconds)
      }

}
