package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.scenari.common.CommonSteps
import org.apache.james.gatling.jmap.{HttpSettings, JmapMessages}

import scala.concurrent.duration._

class JmapMessageUpdateScenario extends Simulation {
  private val loopVariableName = "any"

  val userCount = 100

  val feeder = UserFeeder.createUserFeederWithInboxAndOutbox(userCount)

  val scn = scenario("JmapUpdateMessages")
    .exec(CommonSteps.provisionUsersWithMessageList(feeder))
    .repeat(250, loopVariableName) {
      randomSwitch(
        70.0 -> exec(JmapMessages.markAsRead()),
        20.0 -> exec(JmapMessages.markAsAnswered()),
        10.0 -> exec(JmapMessages.markAsFlagged())
      )
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)
}
