package org.apache.james.gatling.simulation.jmap

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.scenari.JmapBigSetScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

class JmapBigSetSimulation extends Simulation {

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)

  private val scenario = new JmapBigSetScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, Configuration.NumberOfMailboxes, Configuration.NumberOfMessages, users)
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}