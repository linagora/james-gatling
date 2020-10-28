package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.scenari.JmapCountMailboxesScenario

import scala.concurrent.duration._

class JmapCountMailboxesIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((feederBuilder, _) => {
    new JmapCountMailboxesScenario().generate(10 seconds, feederBuilder)
  })
}
