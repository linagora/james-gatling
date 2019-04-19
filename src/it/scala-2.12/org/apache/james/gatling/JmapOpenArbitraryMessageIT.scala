package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapOpenArbitraryMessageScenario

class JmapOpenArbitraryMessageIT extends JmapIT {

  before {
    users.foreach(user => server.sendMessage(Fixture.homer)(user.username))
  }

  scenario(feederBuilder => {
    new JmapOpenArbitraryMessageScenario().generate(feederBuilder)
  })

}
