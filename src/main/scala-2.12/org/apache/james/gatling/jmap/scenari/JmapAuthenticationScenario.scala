package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.CommonSteps

class JmapAuthenticationScenario {

  def generate(userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("JmapAuthentication")
      .feed(userFeeder)
      .exec(CommonSteps.authentication())

}
