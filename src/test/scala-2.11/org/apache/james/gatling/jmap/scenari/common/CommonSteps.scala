package org.apache.james.gatling.jmap.scenari.common

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.{JmapAuthentication, JmapMailboxes}

import scala.concurrent.duration._
import scala.concurrent.Future
import org.apache.james.gatling.control.User

object CommonSteps {

  def authentication(users: Seq[Future[User]]) =
    scenario("JmapAuthentication")
    .feed(UserFeeder.createCompletedUserFeederWithInboxAndOutbox(users))
    .pause(1 second, 30 second)
    .exec(JmapAuthentication.authentication())
    .pause(1 second)

  def provisionSystemMailboxes(users: Seq[Future[User]]) =
    scenario("provisionSystemMailboxes")
      .exec(authentication(users))
      .pause(1 second)
      .exec(JmapMailboxes.getSystemMailboxes)

}
