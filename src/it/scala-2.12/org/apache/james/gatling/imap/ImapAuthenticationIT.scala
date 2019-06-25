package org.apache.james.gatling.imap

import org.apache.james.gatling.imap.scenari.ImapAuthenticationScenario

import scala.concurrent.duration._


class ImapAuthenticationIT extends ImapIT {
  scenario(feederBuilder => {
    new ImapAuthenticationScenario().generate(10 seconds, feederBuilder)
  })
}
