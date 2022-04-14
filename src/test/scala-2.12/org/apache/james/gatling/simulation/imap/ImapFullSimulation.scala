package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.imap.scenari.{ImapFullHeavyUserScenario, ImapFullLightUserScenario}
import org.apache.james.gatling.simulation.Configuration.UserCount
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederWebAdminFactory}

class ImapFullSimulation extends Simulation {
  private val lightScenario: ImapFullLightUserScenario = new ImapFullLightUserScenario()
  private val heavyScenario: ImapFullHeavyUserScenario = new ImapFullHeavyUserScenario()
  private val feederFactory: UsersFeederWebAdminFactory = new UsersFeederWebAdminFactory(UserCount).initUsers

  setUp(
    lightScenario.generate(Configuration.ScenarioDuration, feederFactory.userFeeder()).inject(atOnceUsers((Configuration.UserCount * 0.75).round.toInt)),
    heavyScenario.generate(Configuration.ScenarioDuration, feederFactory.userFeeder()).inject(atOnceUsers((Configuration.UserCount * 0.25).round.toInt))
  ).protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build())
}
