package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.User
import org.apache.james.gatling.jmap.{CommonSteps, JmapMailbox}

import scala.concurrent.Future
import scala.concurrent.duration._

class JmapGetMailboxesScenario {

  def generate(duration: Duration): ScenarioBuilder =
    scenario("JmapGetMailboxes")
      .exec(CommonSteps.authentication())
      .during(duration) {
        JmapMailbox.getSystemMailboxesWithRetryAuthentication
          .pause(1 second , 2 seconds)
      }
}
