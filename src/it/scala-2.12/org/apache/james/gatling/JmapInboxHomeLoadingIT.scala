package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapInboxHomeLoadingScenario

class JmapInboxHomeLoadingIT extends JmapIT(importMessages = true) {

  scenario(feederBuilder => {
    new JmapInboxHomeLoadingScenario().generate(feederBuilder)
  })
}
