package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.draft.JmapIT
import org.apache.james.gatling.jmap.rfc8621.scenari.SelectMailboxScenario

class SelectMailboxIT extends JmapIT {
  private val MIN_MESSAGES_IN_MAILBOX_TO_SELECT = 1
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, _) => {
    new SelectMailboxScenario(MIN_MESSAGES_IN_MAILBOX_TO_SELECT).generate(userFeederBuilder)
  })

}
