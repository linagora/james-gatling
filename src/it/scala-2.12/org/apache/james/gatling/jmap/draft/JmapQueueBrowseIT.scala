package org.apache.james.gatling.jmap.draft

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.control.JamesWebAdministrationQuery
import org.apache.james.gatling.jmap.draft.scenari.JmapQueueBrowseScenario

import scala.concurrent.duration._

class JmapQueueBrowseIT extends JmapIT {

  var webAdmin = new JamesWebAdministrationQuery(server.mappedWebadmin.baseUrl)

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    new JmapQueueBrowseScenario().generate(10 seconds, userFeederBuilder, recipientFeederBuilder, webAdmin)
  })
}
