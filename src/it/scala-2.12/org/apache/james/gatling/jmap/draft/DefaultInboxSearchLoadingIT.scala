package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.scenari.DefaultInboxSearchLoadingScenario

class DefaultInboxSearchLoadingIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((feederBuilder, _) => {
    new DefaultInboxSearchLoadingScenario().generate(feederBuilder)
  })
}
