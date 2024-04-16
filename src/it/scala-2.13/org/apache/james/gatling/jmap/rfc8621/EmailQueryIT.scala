package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.rfc8621.scenari.EmailQueryScenario

import scala.concurrent.duration._

class EmailQueryIT() extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new EmailQueryScenario().generate(10 seconds, userFeederBuilder, recipientFeederBuilder)
  })
}
