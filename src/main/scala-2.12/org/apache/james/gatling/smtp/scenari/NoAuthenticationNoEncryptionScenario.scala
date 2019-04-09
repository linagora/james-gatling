package org.apache.james.gatling.smtp.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control._
import org.apache.james.gatling.smtp.SmtpProtocol.smtp

import scala.concurrent.Future
import scala.concurrent.duration._

class NoAuthenticationNoEncryptionScenario {

  def generate(duration: Duration, users: Seq[Future[User]]): ScenarioBuilder =
    scenario("SMTP_No_Authentication_No_Encryption")
      .feed(UserFeeder.createCompletedUserFeederWithInboxAndOutbox(users))
      .pause(1 second)
      .during(duration) {
        exec(smtp("sendMail")
          .subject("subject")
          .body("This is the mail body being sent"))
          .pause(1 second)
      }
}
