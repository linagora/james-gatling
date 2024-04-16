package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.rfc8621.scenari.EmailGetScenario

import scala.concurrent.duration._

class EmailGetIT extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new EmailGetScenario().generate(10 seconds, userFeederBuilder, recipientFeederBuilder)
  })
}
