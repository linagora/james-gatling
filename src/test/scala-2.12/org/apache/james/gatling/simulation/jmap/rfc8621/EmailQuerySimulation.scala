package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.Predef.{atOnceUsers, _}
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{RecipientFeeder, UserCreator, UserFeeder}
import org.apache.james.gatling.jmap.rfc8621.scenari.EmailQueryScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

class EmailQuerySimulation extends Simulation {
  private val users = Await.result(
    awaitable = Future.sequence(
      new UserCreator(Configuration.BaseJamesWebAdministrationUrl, Configuration.BaseJmapUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)),
    atMost = Inf)

  private val scenario = new EmailQueryScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, UserFeeder.toFeeder(users), RecipientFeeder.usersToFeeder(users))
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}
