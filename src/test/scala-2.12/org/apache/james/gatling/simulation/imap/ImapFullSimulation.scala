package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{UserCreator, UserFeeder}
import org.apache.james.gatling.imap.scenari.{ImapFullHeavyUserScenario, ImapFullLightUserScenario}
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersPerSecond}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import org.apache.james.gatling.simulation.Injection.constantUserPerHour

import scala.concurrent.duration._

class ImapFullSimulation extends Simulation {

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl, Configuration.BaseJmapUrl)
    .createUsersWithInboxAndOutbox(Configuration.UserCount)
  private val usersFeeder = UserFeeder.toFeeder(Await.result(Future.sequence(users), 90 seconds)).circular

  private val lightScenario = new ImapFullLightUserScenario()
  private val heavyScenario = new ImapFullHeavyUserScenario()

  setUp(
    lightScenario.generate(Configuration.ScenarioDuration, usersFeeder)
      .inject(UsersPerSecond(3).injectDuring(Configuration.ScenarioDuration)),
    heavyScenario.generate(Configuration.ScenarioDuration, usersFeeder)
      .inject(UsersPerSecond(1).injectDuring(Configuration.ScenarioDuration))
  ).protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build())
}
