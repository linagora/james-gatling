package org.apache.james.gatling.imap

import org.apache.james.gatling.Fixture
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
