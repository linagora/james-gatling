package org.apache.james.gatling

import org.apache.james.gatling.jmap.scenari.JmapQueueBrowseScenario
import org.apache.james.gatling.control.{JamesWebAdministrationQuery, RandomUserPicker, UserFeeder}

import scala.concurrent.duration._

class JmapQueueBrowseIT extends JmapIT {

  var webAdmin = new JamesWebAdministrationQuery(server.mappedWebadmin.baseUrl)

  before {
    users.foreach(server.importMessages(Fixture.homer))
  }

  scenario(feederBuilder => {
    new JmapQueueBrowseScenario().generate(10 seconds, UserFeeder.toFeeder(users), RandomUserPicker(users), webAdmin)
  })
}
