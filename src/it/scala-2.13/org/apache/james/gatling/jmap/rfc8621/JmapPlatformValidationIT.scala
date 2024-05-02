package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.rfc8621.scenari.JmapPlatformValidationScenario

import scala.concurrent.duration._

class JmapPlatformValidationIT extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new JmapPlatformValidationScenario(minMessagesInMailbox = 1, minWaitDelay = 1 second, maxWaitDelay = 1 second)
      .generate(duration = 10 seconds, userFeederBuilder, recipientFeederBuilder)
  })
}
