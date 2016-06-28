package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.JmapAuthentication.authentication
import org.apache.james.gatling.jmap.{HttpSettings, JmapMailboxes, JmapMessages}

import scala.concurrent.duration._

class JmapSendMessagesScenario extends Simulation {

  val userCount = 100

  val feeder = UserFeeder.createUserFeederWithInboxAndOutbox(userCount)

  val scn = scenario("JmapGetMailboxes")
    .feed(feeder)
    .pause(1 second, 10 second)
    .exec(authentication())
    .pause(1 second)
    .exec(JmapMailboxes.getMailboxes(JmapMailboxes.extractInboxId, JmapMailboxes.extractOutboxId))
    .pause(1 second)
    .repeat(360, "any") {
      exec(JmapMessages.sendMessagesRandomly(feeder))
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)

}
