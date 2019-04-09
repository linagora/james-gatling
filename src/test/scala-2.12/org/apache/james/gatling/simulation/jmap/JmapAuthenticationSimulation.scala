package org.apache.james.gatling.simulation.jmap

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{User, UserCreator, UserFeeder}
import org.apache.james.gatling.jmap.scenari.JmapAuthenticationScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

import scala.concurrent.Future

class JmapAuthenticationSimulation extends Simulation {

  private val users: Seq[Future[User]] = new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)

  private val scenario = new JmapAuthenticationScenario()

  setUp(scenario.generate()
      .feed(UserFeeder.createCompletedUserFeederWithInboxAndOutbox(users))
      .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}