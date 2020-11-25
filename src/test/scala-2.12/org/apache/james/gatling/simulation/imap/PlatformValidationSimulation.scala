package org.apache.james.gatling.simulation.imap

import io.gatling.core.Predef._
import org.apache.james.gatling.imap.scenari.PlatformValidationScenario
import org.apache.james.gatling.simulation.Configuration.{InjectionDuration, ScenarioDuration, UserCount}
import org.apache.james.gatling.simulation.{HttpSettings, SimulationOnMailCorpus}

class PlatformValidationSimulation extends Simulation with SimulationOnMailCorpus {
  setUp(new PlatformValidationScenario()
      .generate(duration = ScenarioDuration, userFeeder = feeder)
    .inject(rampUsers(UserCount) during InjectionDuration)
    .protocols(HttpSettings.httpProtocol))
}
