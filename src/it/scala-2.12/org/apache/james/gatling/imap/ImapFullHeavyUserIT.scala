package org.apache.james.gatling.imap

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.imap.scenari.ImapFullHeavyUserScenario

import scala.concurrent.duration._

class ImapFullHeavyUserIT extends ImapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario(feederBuilder => {
    new ImapFullHeavyUserScenario().generate(10 seconds, feederBuilder)
  })
}

