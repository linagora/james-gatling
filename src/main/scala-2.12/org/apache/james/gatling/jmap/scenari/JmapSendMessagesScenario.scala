package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeeder
import org.apache.james.gatling.jmap.CommonSteps.UserPicker
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

import scala.concurrent.duration._

class JmapSendMessagesScenario {

  def generate(duration: Duration, userFeeder: UserFeeder, userPicker: UserPicker): ScenarioBuilder =
    scenario("JmapSendMessages")
      .feed(userFeeder)
      .exec(CommonSteps.provisionSystemMailboxes())
      .during(duration) {
        exec(JmapMessages.sendMessagesToUserWithRetryAuthentication(userPicker))
          .pause(1 second , 2 seconds)
      }

}
