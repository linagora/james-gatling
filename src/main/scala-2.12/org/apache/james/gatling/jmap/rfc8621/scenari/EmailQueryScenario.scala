package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.CommonSteps
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapHttp, SessionStep}

import scala.concurrent.duration._

class EmailQueryScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("EmailQueryScenario")
      .feed(userFeeder)
      .exec(CommonSteps.provisionUsersWithMessages(recipientFeeder, numberOfMessages = 10))
      .exec(SessionStep.retrieveAccountId)
      .during(duration) {
        exec(JmapEmail.queryEmails()
          .check(JmapHttp.statusOk, JmapHttp.noError, JmapEmail.nonEmptyListMessagesChecks))
          .pause(1 second)
      }
}
