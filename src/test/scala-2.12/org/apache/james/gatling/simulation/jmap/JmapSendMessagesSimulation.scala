package org.apache.james.gatling.simulation.jmap

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{JamesWebAdministrationQuery, UserCreator}
import org.apache.james.gatling.jmap.scenari.JmapSendMessagesScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

class JmapSendMessagesSimulation extends Simulation {

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)
  private val webAdmin = new JamesWebAdministrationQuery(Configuration.BaseJamesWebAdministrationUrl)

  private val scenario = new JmapSendMessagesScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, users)
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}