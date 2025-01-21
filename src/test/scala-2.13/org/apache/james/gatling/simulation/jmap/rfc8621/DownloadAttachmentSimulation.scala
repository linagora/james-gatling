package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.rfc8621.scenari.DownloadAttachmentScenario
import org.apache.james.gatling.simulation.Configuration.UserCount
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederWebAdminFactory}

class DownloadAttachmentSimulation extends Simulation {
  private val scenario: DownloadAttachmentScenario = new DownloadAttachmentScenario()
  private val feederFactory: UsersFeederWebAdminFactory = new UsersFeederWebAdminFactory(UserCount).initUsers

  setUp(scenario.generate(Configuration.ScenarioDuration, feederFactory.userFeeder(), feederFactory.recipientFeeder())
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}
