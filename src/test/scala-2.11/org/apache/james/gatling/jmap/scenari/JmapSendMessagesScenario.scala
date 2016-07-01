package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.scenari.common.CommonSteps
import org.apache.james.gatling.jmap.{HttpSettings, JmapMessages}

import scala.concurrent.duration._

class JmapSendMessagesScenario extends Simulation {

  val userCount = 100
  val feeder = UserFeeder.createUserFeederWithInboxAndOutbox(userCount)
  val loopVariableName = "any"

  val scn = scenario("JmapSendMessages")
    .exec(CommonSteps.provisionSystemMailboxes(feeder))
    .repeat(360, loopVariableName) {
      exec(JmapMessages.sendMessagesRandomly(feeder))
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)

}
