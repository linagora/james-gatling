package org.apache.james.gatling

import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.scenari.JmapSendMessagesScenario

import scala.concurrent.duration._


class JmapSendMessagesIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((feederBuilder, recipientFeederBuilder) => {
    new JmapSendMessagesScenario().generate(10 seconds, UserFeeder.toFeeder(users), recipientFeederBuilder)
  })
}
