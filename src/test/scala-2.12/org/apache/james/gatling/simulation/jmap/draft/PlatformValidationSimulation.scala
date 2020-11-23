package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import org.apache.james.gatling.control.RecipientFeeder
import org.apache.james.gatling.jmap.draft.scenari.PlatformValidationScenario
import org.apache.james.gatling.simulation.Configuration.{InjectionDuration, ScenarioDuration, UserCount}
import org.apache.james.gatling.simulation.{HttpSettings, SimulationOnMailCorpus}

class PlatformValidationSimulation extends Simulation with SimulationOnMailCorpus {
  setUp(new PlatformValidationScenario(minMessagesInMailbox = 10)
      .generate(duration = ScenarioDuration, userFeeder = feeder, recipientFeeder = RecipientFeeder.usersToFeeder(getUsers))
    .inject(rampUsers(UserCount) during InjectionDuration)
    .protocols(HttpSettings.httpProtocol))
}
