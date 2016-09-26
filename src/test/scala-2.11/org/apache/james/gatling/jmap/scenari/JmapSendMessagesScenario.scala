package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.jmap.scenari.common.CommonSteps
import org.apache.james.gatling.jmap.{HttpSettings, JmapMessages}

import scala.concurrent.duration._
import org.apache.james.gatling.control.{Username, Password, User}
import org.apache.james.gatling.control.UserCreator

class JmapSendMessagesScenario extends Simulation {

  val userCount = 100
  val users = UserCreator.createUsersWithInboxAndOutbox(userCount)
  val loopVariableName = "any"

  val scn = scenario("JmapSendMessages")
    .exec(CommonSteps.provisionSystemMailboxes(users))
    .repeat(360, loopVariableName) {
      exec(JmapMessages.sendMessagesRandomly(users))
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)

}
