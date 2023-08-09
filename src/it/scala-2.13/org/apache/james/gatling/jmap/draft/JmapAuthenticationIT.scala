package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.jmap.draft.scenari.JmapAuthenticationScenario

class JmapAuthenticationIT extends JmapIT {
  scenario((userFeederBuilder, _) => new JmapAuthenticationScenario().generate(userFeederBuilder))
}
