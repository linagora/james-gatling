package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.scenari.JmapMessageUpdateScenario

import scala.concurrent.duration._

class JmapMessageUpdateIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new JmapMessageUpdateScenario().generate(10 seconds, userFeederBuilder, recipientFeederBuilder, randomlySentMails = 10)
  })
}
