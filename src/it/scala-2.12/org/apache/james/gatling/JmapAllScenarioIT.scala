package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapAllScenario

import scala.concurrent.duration._

class JmapAllScenarioIT extends JmapIT {
  scenario((userFeederBuilder, recipientFeederBuilder) => new JmapAllScenario().generate(userFeederBuilder, 10.seconds, recipientFeederBuilder))
}
