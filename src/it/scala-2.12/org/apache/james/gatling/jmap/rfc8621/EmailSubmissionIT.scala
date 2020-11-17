package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.jmap.draft.JmapIT
import org.apache.james.gatling.jmap.rfc8621.scenari.EmailSubmissionScenario

import scala.concurrent.duration._

class EmailSubmissionIT extends JmapIT {
  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new EmailSubmissionScenario().generate(10 seconds, userFeederBuilder, recipientFeederBuilder)
  })
}
