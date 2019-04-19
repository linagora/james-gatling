package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeeder
import org.apache.james.gatling.jmap.CommonSteps.UserPicker
import org.apache.james.gatling.jmap.RetryAuthentication._
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

import scala.concurrent.duration._

class JmapGetMessagesScenario {

  def generate(duration: Duration, userFeeder : UserFeeder, userPicker: UserPicker, randomlySentMails: Int): ScenarioBuilder =
    scenario("JmapGetMessages")
      .feed(userFeeder)
      .exec(CommonSteps.provisionUsersWithMessageList(userPicker, randomlySentMails))
      .during(duration) {
        execWithRetryAuthentication(JmapMessages.getRandomMessage(), JmapMessages.getRandomMessageChecks)
          .pause(1 second , 2 seconds)
      }

}
