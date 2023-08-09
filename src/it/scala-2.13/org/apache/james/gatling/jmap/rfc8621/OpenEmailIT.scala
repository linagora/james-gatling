package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.JmapIT
import org.apache.james.gatling.jmap.rfc8621.scenari.OpenEmailScenario

class OpenEmailIT extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, _) => {
    new OpenEmailScenario().generate(userFeederBuilder)
  })
}
