package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.scenari.JmapGetMessageListScenario

import scala.concurrent.duration._

class JmapGetMessageListIT extends JmapIT {
  private val MAILS_NUMBER = 10

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new JmapGetMessageListScenario().generate(10 seconds, userFeederBuilder, recipientFeederBuilder, MAILS_NUMBER)
  })
}
