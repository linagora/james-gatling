package org.apache.james.gatling.jmap.rfc8621

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.jmap.rfc8621.scenari.DownloadAttachmentScenario

import scala.concurrent.duration._

class DownloadAttachmentIT extends JmapIT {
  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new DownloadAttachmentScenario().generate(20 seconds, userFeederBuilder, recipientFeederBuilder,
      provisionMailWithAttachments = Some(true))
  })
}
