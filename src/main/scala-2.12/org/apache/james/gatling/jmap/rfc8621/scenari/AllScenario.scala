package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.CommonSteps
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapHttp, SessionStep}

import scala.concurrent.duration._

class AllScenario {

  def generate(userFeeder: UserFeederBuilder, duration: Duration, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("AllScenarios")
      .feed(userFeeder)
      .exec(CommonSteps.provisionSystemMailboxes())
      .exec(SessionStep.retrieveAccountId)
      .during(duration) {
        exec(JmapEmail.submitEmails(recipientFeeder))
          .pause(1 second, 5 seconds)
          .exec(JmapEmail.queryEmails()
            .check(JmapHttp.statusOk, JmapHttp.noError, JmapEmail.nonEmptyListMessagesChecks()))
          .exec(JmapEmail.getRandomEmails()
            .check(JmapHttp.statusOk, JmapHttp.noError))
          .pause(1 second, 5 seconds)
          .randomSwitch(
            70.0 -> exec(JmapEmail.markAsSeen()),
            20.0 -> exec(JmapEmail.markAsAnswered()),
            10.0 -> exec(JmapEmail.markAsFlagged()))
    }
}
