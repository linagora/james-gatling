package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.jmap.JmapAuthentication.authentication
import org.apache.james.gatling.jmap.{HttpSettings, JmapMailboxes}

import scala.concurrent.duration._

class JmapGetMailboxesScenario extends Simulation {
  val domain = "domain-jmap-getmailboxes.tld"
  val username = "username@" + domain
  val password = "password"

  val scn = scenario("JmapAuthentication")
    .exec(authentication(username, password))
    .pause(1 second)
    .exec(JmapMailboxes.getMailboxes(JmapMailboxes.extractInboxId, JmapMailboxes.extractOutboxId))

  setUp(scn.inject(atOnceUsers(1))).protocols(HttpSettings.httpProtocol)
}
