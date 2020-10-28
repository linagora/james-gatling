package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.RetryAuthentication._
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapMessages}

import scala.concurrent.duration._

class JmapGetMessagesScenario {

  def generate(duration: Duration, userFeeder : UserFeederBuilder, recipientFeeder: RecipientFeederBuilder, randomlySentMails: Int): ScenarioBuilder =
    scenario("JmapGetMessages")
      .feed(userFeeder)
      .exec(CommonSteps.provisionUsersWithMessageList(recipientFeeder, randomlySentMails))
      .during(duration) {
        exec(execWithRetryAuthentication(JmapMessages.getRandomMessages(), JmapMessages.getRandomMessageChecks))
        .pause(1 second , 2 seconds)
      }

}
