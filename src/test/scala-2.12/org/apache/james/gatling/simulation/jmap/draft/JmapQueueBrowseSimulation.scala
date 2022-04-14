package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.JamesWebAdministrationQuery
import org.apache.james.gatling.jmap.draft.scenari.JmapQueueBrowseScenario
import org.apache.james.gatling.simulation.Configuration.UserCount
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederWebAdminFactory}

class JmapQueueBrowseSimulation extends Simulation {
  private val webAdmin: JamesWebAdministrationQuery = new JamesWebAdministrationQuery(Configuration.BaseJamesWebAdministrationUrl)
  private val scenario: JmapQueueBrowseScenario = new JmapQueueBrowseScenario()
  private val feederFactory: UsersFeederWebAdminFactory = new UsersFeederWebAdminFactory(UserCount).initUsers

  setUp(scenario
    .generate(Configuration.ScenarioDuration, feederFactory.userFeeder(), feederFactory.recipientFeeder(), webAdmin)
      .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}