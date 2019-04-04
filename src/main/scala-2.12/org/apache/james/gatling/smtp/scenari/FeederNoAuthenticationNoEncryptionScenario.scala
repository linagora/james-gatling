package org.apache.james.gatling.smtp.scenari

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.smtp.SmtpProtocol.smtp

import scala.concurrent.duration._

class FeederNoAuthenticationNoEncryptionScenario {

  def generate(duration: Duration, feeder: FeederBuilder): ScenarioBuilder =
    scenario("SMTP No authentication no encryption with fixed list of already provisioned users.")
      .feed(feeder)
      .pause(1 second)
      .during(duration) {
        exec(smtp("sendMail")
          .subject("subject")
          .body("This is the mail body being sent"))
          .pause(1 second)
      }
}
