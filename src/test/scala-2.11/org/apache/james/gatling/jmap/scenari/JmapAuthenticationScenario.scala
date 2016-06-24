package org.apache.james.gatling.jmap.scenari

import java.util.UUID

import io.gatling.core.Predef._
import org.apache.james.gatling.control.JamesWebAdministration
import org.apache.james.gatling.jmap.HttpSettings
import org.apache.james.gatling.jmap.JmapAuthentication.authentication

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JmapAuthenticationScenario extends Simulation {

  val userCount = 100

  val domain = UUID.randomUUID().toString

  JamesWebAdministration.addDomain(domain).get

  val loginsAndPassWords = Range.apply(0, userCount)
    .map(i => (UUID.randomUUID().toString + "@" +  domain, UUID.randomUUID().toString))
    .toList

  Future.sequence(
    loginsAndPassWords
      .map(loginAndPassword => JamesWebAdministration.addUser(loginAndPassword._1, loginAndPassword._2)))
    .get

  val feeder = loginsAndPassWords.map(loginAndPassword => Map("username" -> loginAndPassword._1, "password" -> loginAndPassword._2)).toArray

  val scn = scenario("JmapAuthentication")
    .feed(feeder)
    .exec(authentication())

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)

}
