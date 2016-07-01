package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.scenari.common.CommonSteps
import org.apache.james.gatling.jmap.{HttpSettings, JmapMessages}

import scala.concurrent.duration._

class JmapGetMessageListScenario extends Simulation {


  val userCount = 100

  val feeder = UserFeeder.createUserFeederWithInboxAndOutbox(userCount)

  val scn = scenario("JmapGetMessagesLists")
    .exec(CommonSteps.provisionUsersWithMessages(feeder))
    .repeat(250, "any") {
      exec(JmapMessages.listMessages())
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)

}
