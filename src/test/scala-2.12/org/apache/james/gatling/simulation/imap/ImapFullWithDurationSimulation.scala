package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{UserCreator, UserFeeder}
import org.apache.james.gatling.imap.scenari.{ImapFullHeavyUserScenario, ImapFullLightUserScenario}
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersPerSecond}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class ImapFullWithDurationSimulation extends Simulation {
  private val nbUsersPerSecond = 4
  private val duration = 5 minutes

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl, Configuration.BaseJmapUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)
  private val usersFeeder = UserFeeder.toFeeder(Await.result(Future.sequence(users), 90 seconds)).circular

  private val lightScenario = new ImapFullLightUserScenario()
  private val heavyScenario = new ImapFullHeavyUserScenario()

  setUp(
    lightScenario.generate(Configuration.ScenarioDuration, usersFeeder).inject(UsersPerSecond(nbUsersPerSecond * 0.75).injectDuring(duration)),
    heavyScenario.generate(Configuration.ScenarioDuration, usersFeeder).inject(UsersPerSecond(nbUsersPerSecond * 0.25).injectDuring(duration))
  ).protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build())
}
