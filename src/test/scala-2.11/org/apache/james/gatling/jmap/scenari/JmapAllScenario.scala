package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.configuration.Configuration._
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.scenari.common.{CommonSteps, HttpSettings}
import org.apache.james.gatling.jmap.{JmapMailboxes, JmapMessages}

import scala.concurrent.duration._

class JmapAllScenario extends Simulation {
  private val loopVariableName = "any"

  val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)

  val scn = scenario("JmapAllScenarios")
    .exec(CommonSteps.provisionSystemMailboxes(users))
    .during(ScenarioDuration) {
        exec(JmapMessages.sendMessagesRandomlyWithRetryAuthentication(users))
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
