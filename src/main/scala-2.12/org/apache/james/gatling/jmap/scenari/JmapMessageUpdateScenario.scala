package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.jmap.CommonSteps.UserPicker
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

import scala.concurrent.duration._

class JmapMessageUpdateScenario {

  def generate(duration: Duration, userPicker: UserPicker, randomlySentMails: Int): ScenarioBuilder =
    scenario("JmapUpdateMessages")
      .exec(CommonSteps.provisionUsersWithMessageList(userPicker, randomlySentMails))
      .during(duration) {
        randomSwitch(
          70.0 -> exec(JmapMessages.markAsRead()),
          20.0 -> exec(JmapMessages.markAsAnswered()),
          10.0 -> exec(JmapMessages.markAsFlagged())
        )
          .pause(1 second , 2 seconds)
      }
}
