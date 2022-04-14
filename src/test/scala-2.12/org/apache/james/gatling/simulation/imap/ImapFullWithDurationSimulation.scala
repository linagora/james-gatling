package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.imap.scenari.{ImapFullHeavyUserScenario, ImapFullLightUserScenario}
import org.apache.james.gatling.simulation.Configuration.UserCount
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederWebAdminFactory, UsersPerSecond}

import scala.concurrent.duration._

class ImapFullWithDurationSimulation extends Simulation {
  private val nbUsersPerSecond: Int = 4
  private val duration: FiniteDuration = 5 minutes
  private val lightScenario: ImapFullLightUserScenario = new ImapFullLightUserScenario()
  private val heavyScenario: ImapFullHeavyUserScenario = new ImapFullHeavyUserScenario()
  private val feederFactory: UsersFeederWebAdminFactory = new UsersFeederWebAdminFactory(UserCount).initUsers

  setUp(
    lightScenario.generate(Configuration.ScenarioDuration, feederFactory.userFeeder()).inject(UsersPerSecond(nbUsersPerSecond * 0.75).injectDuring(duration)),
    heavyScenario.generate(Configuration.ScenarioDuration, feederFactory.userFeeder()).inject(UsersPerSecond(nbUsersPerSecond * 0.25).injectDuring(duration))
  ).protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build())
}
