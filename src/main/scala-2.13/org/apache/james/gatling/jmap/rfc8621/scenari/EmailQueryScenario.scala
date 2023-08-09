package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapHttp, JmapMailbox, SessionStep}

import scala.concurrent.duration._

class EmailQueryScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("EmailQueryScenario")
      .feed(userFeeder)
      .exec(JmapMailbox.provisionUsersWithMessages(recipientFeeder, numberOfMessages = 10))
      .exec(SessionStep.retrieveAccountId)
      .during(duration.toSeconds.toInt) {
        exec(JmapEmail.queryEmails(JmapEmail.filterKeywordQueryParameter())
          .check(JmapHttp.statusOk, JmapHttp.noError))
          .pause(1 second)
      }
}
