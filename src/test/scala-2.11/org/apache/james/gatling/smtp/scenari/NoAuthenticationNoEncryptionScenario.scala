package org.apache.james.gatling.smtp.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control._
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.smtp.PreDef._

import scala.concurrent.duration._

class NoAuthenticationNoEncryptionScenario extends Simulation {

  val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)

  val scn = scenario("SMTP_No_Authentication_No_Encryption")
    .feed(UserFeeder.createCompletedUserFeederWithInboxAndOutbox(users))
    .pause(10 second, 30 second)
    .during(ScenarioDuration) {
      exec(smtp("sendMail")
        .subject("subject")
        .body("This is the mail body being sent"))
        .pause(1 second)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(smtp)
}
