package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.draft.scenari.JmapGetMailboxesScenario
import org.apache.james.gatling.simulation.Configuration.UserCount
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederWebAdminFactory}

class JmapGetMailboxesSimulation extends Simulation {
  private val scenario: JmapGetMailboxesScenario = new JmapGetMailboxesScenario()
  private val feederFactory: UsersFeederWebAdminFactory = new UsersFeederWebAdminFactory(UserCount).initUsers

  setUp(scenario.generate(Configuration.ScenarioDuration, feederFactory.userFeeder())
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}