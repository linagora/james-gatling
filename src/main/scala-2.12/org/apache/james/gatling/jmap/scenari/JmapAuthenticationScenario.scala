package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.jmap.CommonSteps

class JmapAuthenticationScenario {

  def generate(feederBuilder: FeederBuilder): ScenarioBuilder =
    scenario("JmapAuthentication")
      .feed(feederBuilder)
      .exec(CommonSteps.authentication())

}
