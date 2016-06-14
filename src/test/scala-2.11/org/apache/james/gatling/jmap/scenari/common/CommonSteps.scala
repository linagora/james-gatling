package org.apache.james.gatling.jmap.scenari.common

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.{JmapAuthentication, JmapMailboxes}

import scala.concurrent.duration._

object CommonSteps {

  def authentication(userCount: Int) =
    scenario("JmapAuthentication")
    .feed(UserFeeder.createUserFeederWithInboxAndOutbox(userCount))
    .pause(1 second, 30 second)
    .exec(JmapAuthentication.authentication())
    .pause(1 second)

  def provisionSystemMailboxes(userCount: Int) =
    scenario("provisionSystemMailboxes")
      .exec(authentication(userCount))
      .pause(1 second)
      .exec(JmapMailboxes.getSystemMailboxes)

}
