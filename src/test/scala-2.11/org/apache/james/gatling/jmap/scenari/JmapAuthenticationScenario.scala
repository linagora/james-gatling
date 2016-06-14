package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.jmap.HttpSettings
import org.apache.james.gatling.jmap.scenari.common.CommonSteps

class JmapAuthenticationScenario extends Simulation {

  val userCount = 100

  val scn = scenario("JmapAuthentication")
    .exec(CommonSteps.authentication(userCount))

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)

}
