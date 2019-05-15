package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapCountMailboxesScenario

import scala.concurrent.duration._

class JmapCountMailboxesIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario(feederBuilder => {
    new JmapCountMailboxesScenario().generate(10 seconds, feederBuilder)
  })
}
