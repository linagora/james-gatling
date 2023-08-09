package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.scenari.JmapSelectArbitraryMailboxScenario

class JmapSelectArbitraryMailboxIT extends JmapIT {
  private val MIN_MESSAGES_IN_MAILBOX_TO_SELECT = 1
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, _) => {
    new JmapSelectArbitraryMailboxScenario(MIN_MESSAGES_IN_MAILBOX_TO_SELECT).generate(userFeederBuilder)
  })

}
