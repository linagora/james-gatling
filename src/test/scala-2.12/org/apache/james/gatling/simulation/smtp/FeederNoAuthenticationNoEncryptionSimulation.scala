package org.apache.james.gatling.simulation.smtp

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import org.apache.james.gatling.simulation.Configuration
import org.apache.james.gatling.smtp.SmtpProtocol.smtp
import org.apache.james.gatling.smtp.scenari.SmtpNoAuthenticationNoEncryptionScenario

import scala.concurrent.duration._

class FeederNoAuthenticationNoEncryptionSimulation extends Simulation {

  private val feeder: FeederBuilderBase[String] = csv("users.csv").circular

  private val scenario = new SmtpNoAuthenticationNoEncryptionScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, feeder)
    .inject(nothingFor(10 seconds), rampUsers(Configuration.UserCount) during(10 seconds)))
    .protocols(smtp)
}
