package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.rfc8621.scenari.{InboxLoadingScenario}

class InboxLoadingIT extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, _) => {
    new InboxLoadingScenario().generate(userFeederBuilder)
  })
}
