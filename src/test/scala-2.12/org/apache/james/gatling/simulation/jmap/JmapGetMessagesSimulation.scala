package org.apache.james.gatling.simulation.jmap

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{RandomUserPicker, UserCreator, UserFeeder}
import org.apache.james.gatling.jmap.scenari.JmapGetMessagesScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

class JmapGetMessagesSimulation extends Simulation {

  private val users = Await.result(
    awaitable = Future.sequence(
      new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)),
    atMost = Inf)

  private val scenario = new JmapGetMessagesScenario()

  setUp(scenario
    .generate(Configuration.ScenarioDuration, RandomUserPicker(users), Configuration.RandomlySentMails)
      .feed(UserFeeder.toFeeder(users))
      .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}