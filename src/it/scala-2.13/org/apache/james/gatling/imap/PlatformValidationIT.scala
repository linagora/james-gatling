package org.apache.james.gatling.imap

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.imap.scenari.PlatformValidationScenario

import scala.concurrent.duration._

class PlatformValidationIT extends ImapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario(feederBuilder => {
    new PlatformValidationScenario(minWaitDelay = 1 second, maxWaitDelay = 1 second)
      .generate(10 seconds, feederBuilder)
  })
}
