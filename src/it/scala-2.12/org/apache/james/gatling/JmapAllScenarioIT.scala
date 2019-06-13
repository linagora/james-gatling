package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapAllScenario

import scala.concurrent.duration._

class JmapAllScenarioIT extends JmapIT {
  scenario((feederBuilder, recipientFeederBuilder) => new JmapAllScenario().generate(feederBuilder, 10.seconds, recipientFeederBuilder))
}
