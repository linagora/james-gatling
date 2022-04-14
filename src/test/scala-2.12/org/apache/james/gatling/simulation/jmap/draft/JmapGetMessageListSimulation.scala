package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.draft.scenari.JmapGetMessageListScenario
import org.apache.james.gatling.simulation.Configuration.UserCount
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederWebAdminFactory}

class JmapGetMessageListSimulation extends Simulation {
  private val scenario: JmapGetMessageListScenario = new JmapGetMessageListScenario()
  private val feederFactory: UsersFeederWebAdminFactory = new UsersFeederWebAdminFactory(UserCount).initUsers

  setUp(scenario
    .generate(Configuration.ScenarioDuration, feederFactory.userFeeder(), feederFactory.recipientFeeder(), Configuration.RandomlySentMails)
      .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}