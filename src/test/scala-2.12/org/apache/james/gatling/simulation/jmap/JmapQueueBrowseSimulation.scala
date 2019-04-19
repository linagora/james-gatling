package org.apache.james.gatling.simulation.jmap

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{JamesWebAdministrationQuery, RandomUserPicker, UserCreator, UserFeeder}
import org.apache.james.gatling.jmap.scenari.JmapQueueBrowseScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

class JmapQueueBrowseSimulation extends Simulation {

  private val users = Await.result(
    awaitable = Future.sequence(
      new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)),
    atMost = Inf)

  private val webAdmin = new JamesWebAdministrationQuery(Configuration.BaseJamesWebAdministrationUrl)

  private val scenario = new JmapQueueBrowseScenario()

  setUp(scenario
    .generate(Configuration.ScenarioDuration, UserFeeder.toFeeder(users), RandomUserPicker(users), webAdmin)
      .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}