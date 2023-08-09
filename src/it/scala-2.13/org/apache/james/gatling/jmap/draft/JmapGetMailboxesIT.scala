package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.scenari.JmapGetMailboxesScenario

import scala.concurrent.duration._

class JmapGetMailboxesIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, _) => {
    new JmapGetMailboxesScenario().generate(10 seconds, userFeederBuilder)
  })
}
