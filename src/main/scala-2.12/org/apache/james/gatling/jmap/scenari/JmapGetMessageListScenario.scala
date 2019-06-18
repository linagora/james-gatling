package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.RetryAuthentication._
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

import scala.concurrent.duration._

class JmapGetMessageListScenario {

  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder, randomlySentMails: Int): ScenarioBuilder =
    scenario("JmapGetMessagesLists")
    .feed(userFeeder)
    .exec(CommonSteps.provisionUsersWithMessages(recipientFeeder, randomlySentMails))
    .during(duration) {
      execWithRetryAuthentication(JmapMessages.listMessages(), JmapMessages.listMessagesChecks)
        .pause(1 second , 2 seconds)
    }

}
