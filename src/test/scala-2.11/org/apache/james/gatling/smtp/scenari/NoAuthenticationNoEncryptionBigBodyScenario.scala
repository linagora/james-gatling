package org.apache.james.gatling.smtp.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control._
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.smtp.PreDef._

import scala.concurrent.duration._
import scala.util.Random

class NoAuthenticationNoEncryptionBigBodyScenario extends Simulation {

  val myRandom = Random.alphanumeric

  def generateMessage() : String =
    myRandom grouped 200 map (_ append Stream('\r', '\n')) flatMap identity take (1024 * 1024) mkString

  val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)

  val scn = scenario("SMTP_No_Authentication_No_Encryption_Big_Body")
    .feed(UserFeeder.createCompletedUserFeederWithInboxAndOutbox(users))
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(smtp("sendMail")
        .subject("subject")
        .body(generateMessage()))
        .pause(1 second)
    }

  setUp(scn.inject(nothingFor(10 seconds), rampUsers(UserCount) over(10 seconds))).protocols(smtp)
}
