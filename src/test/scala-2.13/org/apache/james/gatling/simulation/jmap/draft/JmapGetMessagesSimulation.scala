package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.draft.scenari.JmapGetMessagesScenario
import org.apache.james.gatling.simulation.Configuration.UserCount
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederWebAdminFactory}

class JmapGetMessagesSimulation extends Simulation {
  private val scenario: JmapGetMessagesScenario = new JmapGetMessagesScenario()
  private val feederFactory: UsersFeederWebAdminFactory = new UsersFeederWebAdminFactory(UserCount).initUsers

  setUp(scenario
    .generate(Configuration.ScenarioDuration, feederFactory.userFeeder(),
      feederFactory.recipientFeeder(), Configuration.RandomlySentMails)
      .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}