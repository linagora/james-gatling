package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.{Password, User, Username}
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.jmap.scenari.common.{CommonSteps, HttpSettings}
import org.apache.james.gatling.jmap.{JmapMailboxes, JmapMessages}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class FeederJmapAllScenario extends Simulation {
  private val loopVariableName = "any"

  val users = csv("users.csv").circular.records
    .map({ record => User(Username(record("username")), Password(record("password"))) })
    .map(Future.apply(_))
    .seq

  val scn = scenario("FeederJmapAllScenarios")
    .during(ScenarioDuration) {
        exec(CommonSteps.authentication(users))
        .exec(JmapMessages.sendMessagesRandomlyWithRetryAuthentication(users))
        .pause(1 second, 5 seconds)
        .exec(JmapMailboxes.getSystemMailboxesWithRetryAuthentication)
        .exec(JmapMessages.listMessagesWithRetryAuthentication())
        .exec(JmapMessages.getMessagesWithRetryAuthentication())
        .pause(1 second, 5 seconds)
        .randomSwitch(
          70.0 -> exec(JmapMessages.markAsRead()),
          20.0 -> exec(JmapMessages.markAsAnswered()),
          10.0 -> exec(JmapMessages.markAsFlagged())
        )
    }

  setUp(
    scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)
}
