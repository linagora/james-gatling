package org.apache.james.gatling.smtp.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control._
import org.apache.james.gatling.smtp.SmtpProtocol.smtp

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

class NoAuthenticationNoEncryptionBigBodyScenario {

  private val myRandom = Random.alphanumeric

  private def generateMessage() : String =
    myRandom.grouped(200).flatMap(_.append(Stream('\r', '\n'))).take(1024 * 1024).mkString

  def generate(duration: Duration): ScenarioBuilder =
    scenario("SMTP_No_Authentication_No_Encryption_Big_Body")
    .pause(1.second)
    .during(duration) {
      exec(smtp("sendMail")
        .subject("subject")
        .body(generateMessage()))
        .pause(1.second)
    }
}
