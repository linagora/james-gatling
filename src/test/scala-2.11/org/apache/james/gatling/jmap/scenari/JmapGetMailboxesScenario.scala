package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.JmapAuthentication.authentication
import org.apache.james.gatling.jmap.{HttpSettings, JmapMailboxes}

import scala.concurrent.duration._

class JmapGetMailboxesScenario extends Simulation {
  val userCount = 200

  val scn = scenario("JmapGetMailboxes")
    .feed(UserFeeder.createUserFeederWithInboxAndOutbox(userCount))
    .pause(1 second, 30 second)
    .exec(authentication())
    .pause(1 second)
    .repeat(360, "any") {
      exec(JmapMailboxes.getMailboxes(JmapMailboxes.extractInboxId, JmapMailboxes.extractOutboxId))
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(userCount))).protocols(HttpSettings.httpProtocol)
}
