package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.JmapIT
import org.apache.james.gatling.jmap.rfc8621.scenari.AllScenario

import scala.concurrent.duration._

class AllIT extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new AllScenario().generate(userFeederBuilder, 10 seconds, recipientFeederBuilder)
  })
}
