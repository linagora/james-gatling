package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapAllScenario

import scala.concurrent.duration._


class JmapAllIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new JmapAllScenario().generate(userFeederBuilder, 10 seconds, recipientFeederBuilder)
  })
}
