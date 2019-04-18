package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapInboxHomeLoadingScenario

class JmapInboxHomeLoadingIT extends JmapIT {

  before {
    users.foreach(server.importMessages(Fixture.homer))
  }

  scenario(feederBuilder => {
    new JmapInboxHomeLoadingScenario().generate(feederBuilder)
  })
}
