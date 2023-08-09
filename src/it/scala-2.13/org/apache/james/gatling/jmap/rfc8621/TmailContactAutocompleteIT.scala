package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.jmap.draft.JmapIT
import org.apache.james.gatling.jmap.rfc8621.scenari.TmailContactAutocompleteScenario

import scala.concurrent.duration.DurationInt

class TmailContactAutocompleteIT extends JmapIT {
  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new TmailContactAutocompleteScenario().generate(10 seconds, userFeederBuilder, recipientFeederBuilder)
  })
}
