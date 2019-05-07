package org.apache.james.gatling

import org.apache.james.gatling.control.{RandomUserPicker, UserFeeder}
import org.apache.james.gatling.jmap.scenari.JmapMessageUpdateScenario

import scala.concurrent.duration._

class JmapMessageUpdateIT extends JmapIT {

  before {
    users.foreach(server.importMessages(Fixture.homer))
  }

  scenario(feederBuilder => {
    new JmapMessageUpdateScenario().generate(10 seconds, UserFeeder.toFeeder(users), RandomUserPicker(users), 10)
  })
}
