package org.apache.james.gatling.simulation.smtp

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.simulation.Configuration
import org.apache.james.gatling.smtp.SmtpProtocol.smtp
import org.apache.james.gatling.smtp.scenari.NoAuthenticationNoEncryptionScenario

import scala.concurrent.duration._

class NoAuthenticationNoEncryptionBigBodySimulation extends Simulation {

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)


  private val scenario = new NoAuthenticationNoEncryptionScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, users)
    .inject(nothingFor(10 seconds), rampUsers(Configuration.UserCount) during(10 seconds)))
    .protocols(smtp)
}
