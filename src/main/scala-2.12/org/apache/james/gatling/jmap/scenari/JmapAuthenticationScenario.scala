package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.{User, UserCreator}
import org.apache.james.gatling.jmap.CommonSteps

import scala.concurrent.Future

class JmapAuthenticationScenario {

  def generate(): ScenarioBuilder =
    scenario("JmapAuthentication")
    .exec(CommonSteps.authentication())

}
