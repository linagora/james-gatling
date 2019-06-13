package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeeder
import org.apache.james.gatling.jmap.RetryAuthentication._
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

import scala.concurrent.duration._

class JmapGetMessageListScenario {

  def generate(duration: Duration, userFeeder: UserFeeder, recipientFeeder: FeederBuilder, randomlySentMails: Int): ScenarioBuilder =
    scenario("JmapGetMessagesLists")
    .feed(userFeeder)
    .exec(CommonSteps.provisionUsersWithMessages(randomlySentMails))
    .during(duration) {
      feed(recipientFeeder)
      execWithRetryAuthentication(JmapMessages.listMessages(), JmapMessages.listMessagesChecks)
        .pause(1 second , 2 seconds)
    }

}
