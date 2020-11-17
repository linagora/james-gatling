package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.CommonSteps
import org.apache.james.gatling.jmap.rfc8621.JmapEmail
import org.apache.james.gatling.jmap.rfc8621.SessionStep.retrieveAccountId

import scala.concurrent.duration._

class EmailSubmissionScenario {

  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("EmailSubmissionScenario")
      .feed(userFeeder)
      .exec(CommonSteps.provisionSystemMailboxes())
      .exec(retrieveAccountId)
      .during(duration) {
        exec(JmapEmail.submitEmails(recipientFeeder))
          .pause(1 second, 2 seconds)
      }

}
