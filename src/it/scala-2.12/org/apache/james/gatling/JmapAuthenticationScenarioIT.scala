package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapAuthenticationScenario

class JmapAuthenticationScenarioIT extends JmapIT {
  scenario(feederBuilder => new JmapAuthenticationScenario().generate(feederBuilder))
}
