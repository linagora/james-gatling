package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.control.UserFeeder.UserFeeder
import org.apache.james.gatling.jmap.JmapMailboxes
import org.apache.james.gatling.jmap.scenari.common.CommonSteps
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.jmap.scenari.common.HttpSettings
import scala.concurrent.duration._
import org.apache.james.gatling.control.UserCreator

class JmapGetMailboxesScenario extends Simulation {

  val scn = scenario("JmapGetMailboxes")
    .exec(CommonSteps.authentication(new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)))
    .during(ScenarioDuration) {
      exec(JmapMailboxes.getSystemMailboxes)
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)
}
