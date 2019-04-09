package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.{Password, User, Username}
import org.apache.james.gatling.jmap.{CommonSteps, JmapMailboxes, JmapMessages}

import scala.concurrent.Future
import scala.concurrent.duration._

class FeederJmapAllScenario {
  private val loopVariableName = "any"

  def generate(duration: Duration, users: Seq[Future[User]]): ScenarioBuilder =
    scenario("FeederJmapAllScenarios")
      .during(duration) {
        exec(CommonSteps.authentication())
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

}
