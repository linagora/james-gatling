package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.User
import org.apache.james.gatling.jmap.{CommonSteps, JmapMailboxes}

import scala.concurrent.Future
import scala.concurrent.duration._

class JmapGetMailboxesScenario extends Simulation {

  def generate(duration: Duration, users: Seq[Future[User]]): ScenarioBuilder =
    scenario("JmapGetMailboxes")
      .exec(CommonSteps.authentication(users))
      .during(duration) {
        JmapMailboxes.getSystemMailboxesWithRetryAuthentication
          .pause(1 second , 2 seconds)
      }
}
