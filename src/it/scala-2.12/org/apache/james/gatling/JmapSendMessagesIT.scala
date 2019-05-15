package org.apache.james.gatling

import org.apache.james.gatling.control.{RandomUserPicker, UserFeeder}
import org.apache.james.gatling.jmap.scenari.JmapSendMessagesScenario

import scala.concurrent.duration._


class JmapSendMessagesIT extends JmapIT {

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  scenario(feederBuilder => {
    new JmapSendMessagesScenario().generate(10 seconds, UserFeeder.toFeeder(users), RandomUserPicker(users))
  })
}
