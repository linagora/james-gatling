package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.jmap.draft.scenari.JmapAllScenario

import scala.concurrent.duration._

class JmapAllScenarioIT extends JmapIT {
  scenario((userFeederBuilder, recipientFeederBuilder) => new JmapAllScenario().generate(userFeederBuilder, 10.seconds, recipientFeederBuilder))
}
