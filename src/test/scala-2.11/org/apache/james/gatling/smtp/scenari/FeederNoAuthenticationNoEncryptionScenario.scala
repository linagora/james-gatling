package org.apache.james.gatling.smtp.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control._
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.smtp.scenari.common.Configuration._

import scala.concurrent.duration._

class FeederNoAuthenticationNoEncryptionScenario extends Simulation {

  def feeder = csv("users.csv")

  val scn = scenario("SMTP No authentication no encryption with fixed list of already provisioned users.")
    .feed(feeder)
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(smtp("sendMail")
        .subject("subject")
        .body("This is the mail body being sent"))
        .pause(1 second)
    }

  setUp(scn.inject(nothingFor(10 seconds), rampUsers(UserCount) over(10 seconds))).protocols(smtp)
}
