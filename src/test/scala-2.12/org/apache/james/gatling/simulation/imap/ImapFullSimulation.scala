package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{UserCreator, UserFeeder}
import org.apache.james.gatling.imap.scenari.ImapFullScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class ImapFullSimulation extends Simulation {

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)
  private val usersFeeder = UserFeeder.toFeeder(Await.result(Future.sequence(users), 30 seconds))

  private val scenario = new ImapFullScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, usersFeeder)
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol, imap.host(Configuration.ServerHostName).build())
}