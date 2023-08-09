package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.draft.scenari.JmapCountMailboxesScenario
import org.apache.james.gatling.simulation.Configuration.UserCount
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederWebAdminFactory}

class JmapCountMailboxesSimulation extends Simulation {
  private val scenario: JmapCountMailboxesScenario = new JmapCountMailboxesScenario()
  private val feederFactory: UsersFeederWebAdminFactory = new UsersFeederWebAdminFactory(UserCount).initUsers

  setUp(scenario.generate(Configuration.ScenarioDuration, feederFactory.userFeeder())
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}