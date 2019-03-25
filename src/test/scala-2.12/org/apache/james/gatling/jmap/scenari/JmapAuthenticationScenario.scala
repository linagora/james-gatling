package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.jmap.scenari.common.{CommonSteps, HttpSettings}

class JmapAuthenticationScenario extends Simulation {

  val scn = scenario("JmapAuthentication")
    .exec(CommonSteps.authentication(new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)))

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)

}
