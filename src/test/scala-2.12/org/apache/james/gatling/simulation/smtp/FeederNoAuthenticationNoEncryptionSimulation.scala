package org.apache.james.gatling.simulation.smtp

import io.gatling.core.Predef._
import org.apache.james.gatling.simulation.Configuration
import org.apache.james.gatling.smtp.SmtpProtocol.smtp
import org.apache.james.gatling.smtp.scenari.FeederNoAuthenticationNoEncryptionScenario

import scala.concurrent.duration._

class FeederNoAuthenticationNoEncryptionSimulation extends Simulation {

  private val feeder = csv("users.csv")

  private val scenario = new FeederNoAuthenticationNoEncryptionScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, feeder)
    .inject(nothingFor(10 seconds), rampUsers(Configuration.UserCount) during(10 seconds)))
    .protocols(smtp)
}
