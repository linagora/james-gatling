package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapGetMailboxesScenario

import scala.concurrent.duration._

class JmapGetMailboxesIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario(feederBuilder => {
    new JmapGetMailboxesScenario().generate(10 seconds, feederBuilder)
  })
}
