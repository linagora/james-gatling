package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.draft.scenari.JmapAllScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederCSVFactory}

class FeederJmapAllSimulation extends Simulation {

  private val scenario = new JmapAllScenario()
  private val feederFactory: UsersFeederCSVFactory = new UsersFeederCSVFactory().loadUsers

  setUp(scenario
    .generate(feederFactory.userFeeder(),
      Configuration.ScenarioDuration,
      feederFactory.recipientFeeder())
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}