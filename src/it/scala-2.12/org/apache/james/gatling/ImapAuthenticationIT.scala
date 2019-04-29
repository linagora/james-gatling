package org.apache.james.gatling

import org.apache.james.gatling.imap.scenari.ImapAuthenticationScenario
import scala.concurrent.duration._


class ImapAuthenticationIT extends ImapIT {

  before {
    users.foreach(server.importMessages(Fixture.homer))
  }

  scenario(feederBuilder => {
    new ImapAuthenticationScenario().generate(10 seconds, feederBuilder)
  })
}
