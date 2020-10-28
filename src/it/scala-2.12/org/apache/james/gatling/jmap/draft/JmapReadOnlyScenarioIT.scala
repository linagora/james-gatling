package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.jmap.draft.scenari.JmapReadOnlyScenario

import scala.concurrent.duration._

class JmapReadOnlyScenarioIT extends JmapIT {
  scenario(authenticatedUserFeederBuilder => new JmapReadOnlyScenario().generate(authenticatedUserFeederBuilder, 5.seconds))
}
