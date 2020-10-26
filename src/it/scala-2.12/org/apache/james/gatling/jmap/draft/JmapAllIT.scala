package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.scenari.JmapAllScenario

import scala.concurrent.duration._


class JmapAllIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new JmapAllScenario().generate(userFeederBuilder, 10 seconds, recipientFeederBuilder)
  })
}
