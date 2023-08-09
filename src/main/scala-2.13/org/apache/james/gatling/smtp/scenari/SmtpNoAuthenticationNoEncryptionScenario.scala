package org.apache.james.gatling.smtp.scenari

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.smtp.SmtpProtocol.smtp

import scala.concurrent.duration._

class SmtpNoAuthenticationNoEncryptionScenario {
  def generate(duration: Duration, feeder: FeederBuilderBase[String]): ScenarioBuilder =
    scenario("SMTP_No_Authentication_No_Encryption")
    .feed(feeder)
    .pause(1 second)
    .during(duration.toSeconds.toInt) {
      exec(smtp("sendMail")
        .subject("subject")
        .body("This is the mail body being sent"))
        .pause(1 second)
    }
}
