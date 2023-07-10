package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import org.apache.james.gatling.jmap.draft.scenari.PlatformValidationScenario
import org.apache.james.gatling.simulation.Configuration.{InjectionDuration, MaxDuration, ScenarioDuration, UserCount}
import org.apache.james.gatling.simulation.{HttpSettings, UsersFeederCSVFactory}

class PlatformValidationSimulation extends Simulation {
  private val feederFactory: UsersFeederCSVFactory = new UsersFeederCSVFactory().loadUsers

  setUp(new PlatformValidationScenario(minMessagesInMailbox = 10)
    .generate(duration = ScenarioDuration,
      userFeeder = feederFactory.userFeeder(),
      recipientFeeder = feederFactory.recipientFeeder())
    .inject(rampUsers(UserCount) during InjectionDuration)
    .protocols(HttpSettings.httpProtocol))
    .maxDuration(MaxDuration)
}
