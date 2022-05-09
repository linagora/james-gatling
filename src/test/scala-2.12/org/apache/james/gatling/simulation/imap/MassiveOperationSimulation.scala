package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.Predef._
import org.apache.james.gatling.imap.scenari.MassiveOperationScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersFeederCSVFactory}

import scala.concurrent.duration._

class MassiveOperationSimulation extends Simulation {
  private val feederFactory: UsersFeederCSVFactory = new UsersFeederCSVFactory().loadUsers
  private val usersCount = 1
  private val injectionDuration = 1 minutes

  setUp(new MassiveOperationScenario()
    .generate(feeder = feederFactory.userFeeder())
    .inject(rampUsers(usersCount) during injectionDuration)
    .protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build()))
}
