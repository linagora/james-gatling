package org.apache.james.gatling.jmap

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.scenari.JmapGetMessagesScenario

import scala.concurrent.duration._

class JmapGetMessagesIT extends JmapIT {
  private val MAILS_NUMBER = 10

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new JmapGetMessagesScenario().generate(10 seconds, userFeederBuilder, recipientFeederBuilder, MAILS_NUMBER)
  })
}
