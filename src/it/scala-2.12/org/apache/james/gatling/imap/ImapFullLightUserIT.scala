package org.apache.james.gatling.imap

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.imap.scenari.ImapFullLightUserScenario

import scala.concurrent.duration._

class ImapFullLightUserIT extends ImapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario(feederBuilder => {
    new ImapFullLightUserScenario().generate(10 seconds, feederBuilder)
  })
}
