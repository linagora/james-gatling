package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapMailbox}

import scala.concurrent.duration._

class JmapGetMailboxesScenario {

  def generate(duration: Duration, userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("JmapGetMailboxes")
      .feed(userFeeder)
      .exec(CommonSteps.authentication())
      .during(duration.toSeconds.toInt) {
        JmapMailbox.getSystemMailboxesWithRetryAuthentication
          .pause(1 second , 2 seconds)
      }
}
