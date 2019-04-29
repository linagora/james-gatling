package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapCountMailboxesScenario

import scala.concurrent.duration._

class JmapCountMailboxesIT extends JmapIT {

  before {
    users.foreach(server.importMessages(Fixture.homer))
  }

  scenario(feederBuilder => {
    new JmapCountMailboxesScenario().generate(10 seconds, feederBuilder)
  })
}
