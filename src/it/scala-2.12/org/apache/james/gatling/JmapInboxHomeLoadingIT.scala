package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapInboxHomeLoadingScenario

class JmapInboxHomeLoadingIT extends JmapIT {

  before {
    users.foreach(user => server.sendMessage(Fixture.homer)(user.username))
  }

  scenario(feederBuilder => {
    new JmapInboxHomeLoadingScenario().generate(feederBuilder)
  })
}
