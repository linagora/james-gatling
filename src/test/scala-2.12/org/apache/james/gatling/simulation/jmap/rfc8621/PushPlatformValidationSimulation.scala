package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.rfc8621.scenari.PushPlatformValidationScenario
import org.apache.james.gatling.simulation.Configuration.{InjectionDuration, ScenarioDuration, UserCount}
import org.apache.james.gatling.simulation.{HttpSettings, UsersFeederCSVFactory}

class PushPlatformValidationSimulation extends Simulation {
  private val feederFactory: UsersFeederCSVFactory = new UsersFeederCSVFactory().loadUsers

  setUp(new PushPlatformValidationScenario(minMessagesInMailbox = 10)
    .generate(duration = ScenarioDuration,
      userFeeder = feederFactory.userFeeder(),
      recipientFeeder = feederFactory.recipientFeeder())
    .inject(rampUsers(UserCount) during InjectionDuration)
    .protocols(HttpSettings.httpProtocol))
}
