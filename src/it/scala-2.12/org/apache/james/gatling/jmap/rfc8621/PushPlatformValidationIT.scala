package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.JmapIT
import org.apache.james.gatling.jmap.scenari.PushPlatformValidationScenario

import scala.concurrent.duration._

class PushPlatformValidationIT extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new PushPlatformValidationScenario(minMessagesInMailbox = 1).generate(duration = 10 seconds, userFeederBuilder, recipientFeederBuilder)
  })
}
