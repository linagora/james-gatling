package org.apache.james.gatling

import org.apache.james.gatling.control.{RandomUserPicker, UserFeeder}
import org.apache.james.gatling.jmap.scenari.JmapGetMessageListScenario

import scala.concurrent.duration._

class JmapGetMessageListIT extends JmapIT {
  private val MAILS_NUMBER = 10

  before {
    users.foreach(server.importMessages(Fixture.homer))
  }

  scenario(feederBuilder => {
    new JmapGetMessageListScenario().generate(10 seconds, UserFeeder.toFeeder(users), RandomUserPicker(users), MAILS_NUMBER)
  })
}
