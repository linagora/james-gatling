package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapSelectArbitraryMaiboxScenario

class JmapSelectArbitraryMailboxIT extends JmapIT {
  private val MIN_MESSAGES_IN_MAILBOX_TO_SELECT = 1
  before {
    users.foreach(user => server.sendMessage(Fixture.homer)(user.username))
  }

  scenario(feederBuilder => {
    new JmapSelectArbitraryMaiboxScenario(MIN_MESSAGES_IN_MAILBOX_TO_SELECT).generate(feederBuilder)
  })

}
