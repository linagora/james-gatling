package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.JmapMailboxes
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.jmap.scenari.common.{CommonSteps, HttpSettings}

import scala.concurrent.duration._

class JmapGetMailboxesScenario extends Simulation {

  val scn = scenario("JmapGetMailboxes")
    .exec(CommonSteps.authentication(new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithSystemMailboxes(UserCount)))
    .during(ScenarioDuration) {
      JmapMailboxes.getSystemMailboxesWithRetryAuthentication
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)
}
