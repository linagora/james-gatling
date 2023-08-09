package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapHttp, JmapMailbox, SessionStep}

import scala.concurrent.duration._

class EmailGetScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("EmailGetScenario")
      .feed(userFeeder)
      .exec(SessionStep.retrieveAccountId)
      .exec(JmapMailbox.provisionUsersWithMessages(recipientFeeder, numberOfMessages = 10))
      .during(duration) {
        exec(JmapEmail.queryEmails()
            .check(JmapHttp.statusOk, JmapHttp.noError, JmapEmail.nonEmptyListMessagesChecks()))
          .pause(1 second)
          .exec(JmapEmail.getRandomEmails()
            .check(JmapHttp.statusOk, JmapHttp.noError, JmapEmail.nonEmptyEmailsChecks))
          .pause(1 second)
      }
}
