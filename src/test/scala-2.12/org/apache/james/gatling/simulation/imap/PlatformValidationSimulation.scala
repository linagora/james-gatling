package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.Predef._
import org.apache.james.gatling.imap.scenari.PlatformValidationScenario
import org.apache.james.gatling.simulation.Configuration.{InjectionDuration, MaxDuration, ScenarioDuration, UserCount}
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederCSVFactory}

class PlatformValidationSimulation extends Simulation {
  private val feederFactory: UsersFeederCSVFactory = new UsersFeederCSVFactory().loadUsers

  setUp(new PlatformValidationScenario()
    .generate(duration = ScenarioDuration, userFeeder = feederFactory.userFeeder())
    .inject(rampUsers(UserCount) during InjectionDuration)
    .protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build()))
    .maxDuration(MaxDuration)
}
