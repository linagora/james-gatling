package org.apache.james.gatling

import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.scenari.JmapGetMessageListScenario

import scala.concurrent.duration._

class JmapGetMessageListIT extends JmapIT {
  private val MAILS_NUMBER = 10

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((feederBuilder, recipientFeederBuilder) => {
    new JmapGetMessageListScenario().generate(10 seconds, UserFeeder.toFeeder(users), recipientFeederBuilder, MAILS_NUMBER)
  })
}
