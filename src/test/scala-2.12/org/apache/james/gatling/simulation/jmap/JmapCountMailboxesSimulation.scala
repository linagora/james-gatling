package org.apache.james.gatling.simulation.jmap

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{UserCreator, UserFeeder}
import org.apache.james.gatling.jmap.scenari.JmapCountMailboxesScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

class JmapCountMailboxesSimulation extends Simulation {

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)

  private val scenario = new JmapCountMailboxesScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration)
      .feed(UserFeeder.createCompletedUserFeederWithInboxAndOutbox(users))
      .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}