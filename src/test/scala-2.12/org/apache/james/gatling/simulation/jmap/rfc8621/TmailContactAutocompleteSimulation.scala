package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.rfc8621.scenari.TmailContactAutocompleteScenario
import org.apache.james.gatling.simulation.Configuration.{InjectionDuration, ScenarioDuration, UserCount}
import org.apache.james.gatling.simulation.{HttpSettings, UsersFeederCSVFactory}

class TmailContactAutocompleteSimulation extends Simulation {
  private val scenario: TmailContactAutocompleteScenario = new TmailContactAutocompleteScenario()
  private val feederFactory: UsersFeederCSVFactory = new UsersFeederCSVFactory().loadUsers

  setUp(scenario.generate(
    duration = ScenarioDuration,
    userFeeder = feederFactory.userFeeder(),
    recipientFeeder = feederFactory.recipientFeeder())
    .inject(rampUsers(UserCount) during InjectionDuration)
    .protocols(HttpSettings.httpProtocol))
}
