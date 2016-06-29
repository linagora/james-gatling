package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.JmapAuthentication.authentication
import org.apache.james.gatling.jmap.{HttpSettings, JmapMailboxes, JmapMessages}

import scala.concurrent.duration._

class JmapGetMessagesScenario extends Simulation {


  val userCount = 100

  val feeder = UserFeeder.createUserFeederWithInboxAndOutbox(userCount)

  val scn = scenario("JmapGetMessages")
    .feed(feeder)
    .pause(10 second, 20 second)
    .exec(authentication())
    .pause(1 second)
    .exec(JmapMailboxes.getMailboxes(JmapMailboxes.extractInboxId, JmapMailboxes.extractOutboxId))
    .pause(1 second)
    .repeat(10, "any") {
      exec(JmapMessages.sendMessagesRandomly(feeder))
        .pause(1 second , 2 seconds)
    }
    .pause(30 second)
    .exec(JmapMessages.listMessages())
    .pause(1 second , 2 seconds)
    .repeat(250, "any") {
      exec(JmapMessages.getRandomMessage())
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)

}
