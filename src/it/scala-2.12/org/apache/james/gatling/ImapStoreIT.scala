package org.apache.james.gatling

import org.apache.james.gatling.imap.scenari.ImapStoreScenario
import scala.concurrent.duration._

class ImapStoreIT extends ImapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario(feederBuilder => {
    new ImapStoreScenario().generate(10 seconds, feederBuilder)
  })
}
