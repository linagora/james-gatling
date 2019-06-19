package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapAuthenticationScenario

class JmapAuthenticationIT extends JmapIT {
  scenario((userFeederBuilder, _) => new JmapAuthenticationScenario().generate(userFeederBuilder))
}
