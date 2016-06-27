package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.HttpSettings
import org.apache.james.gatling.jmap.JmapAuthentication.authentication

class JmapAuthenticationScenario extends Simulation {

  val userCount = 100

  val scn = scenario("JmapAuthentication")
    .feed(UserFeeder.createUserFeeder(userCount))
    .exec(authentication())

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)

}
