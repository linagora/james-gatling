package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.RetryAuthentication._
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapMessages}

import scala.concurrent.duration._

class JmapGetMessageListScenario {

  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder, randomlySentMails: Int): ScenarioBuilder =
    scenario("JmapGetMessagesLists")
    .feed(userFeeder)
    .exec(CommonSteps.provisionUsersWithMessages(recipientFeeder, randomlySentMails))
    .during(duration.toSeconds.toInt) {
      execWithRetryAuthentication(JmapMessages.listMessages(), JmapMessages.nonEmptyListMessagesChecks)
        .pause(1 second , 2 seconds)
    }

}
