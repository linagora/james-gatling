package org.apache.james.gatling.simulation.jmap

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{JamesWebAdministrationQuery, UserCreator}
import org.apache.james.gatling.jmap.scenari.JmapQueueBrowseScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

class JmapQueueBrowseSimulation extends Simulation {

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)
  private val webAdmin = new JamesWebAdministrationQuery(Configuration.BaseJamesWebAdministrationUrl)

  private val scenario = new JmapQueueBrowseScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, users, webAdmin)
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}