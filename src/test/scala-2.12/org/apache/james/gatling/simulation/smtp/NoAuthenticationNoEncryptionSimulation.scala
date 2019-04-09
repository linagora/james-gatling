package org.apache.james.gatling.simulation.smtp

import io.gatling.core.Predef._
import org.apache.james.gatling.control.{UserCreator, UserFeeder}
import org.apache.james.gatling.simulation.Configuration
import org.apache.james.gatling.smtp.SmtpProtocol.smtp
import org.apache.james.gatling.smtp.scenari.NoAuthenticationNoEncryptionScenario

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class NoAuthenticationNoEncryptionSimulation extends Simulation {

  private val users = Await.result(
    awaitable = Future.sequence(
      new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)),
    atMost = Inf)

  private val scenario = new NoAuthenticationNoEncryptionScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration)
      .feed(UserFeeder.toFeeder(users))
      .inject(nothingFor(10 seconds), rampUsers(Configuration.UserCount) during(10 seconds)))
    .protocols(smtp)
}
