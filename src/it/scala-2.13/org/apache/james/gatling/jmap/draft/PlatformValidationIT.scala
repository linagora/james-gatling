package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.scenari.PlatformValidationScenario

import scala.concurrent.duration._

class PlatformValidationIT extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new PlatformValidationScenario(minMessagesInMailbox = 1, minWaitDelay = 1 second, maxWaitDelay = 1 second)
      .generate(duration = 60 seconds, userFeederBuilder, recipientFeederBuilder)
  })
}
