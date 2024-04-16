package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.rfc8621.scenari.MailboxGetScenario

import scala.concurrent.duration._

class MailboxGetIT() extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, _) => {
    new MailboxGetScenario().generate(10 seconds, userFeederBuilder)
  })
}
