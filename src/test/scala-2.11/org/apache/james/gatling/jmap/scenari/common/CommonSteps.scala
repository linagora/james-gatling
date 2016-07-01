package org.apache.james.gatling.jmap.scenari.common

import io.gatling.core.Predef._
import org.apache.james.gatling.jmap.{JmapAuthentication, JmapMailboxes, JmapMessages}

import scala.concurrent.duration._
import scala.concurrent.Future
import org.apache.james.gatling.control.User
import org.apache.james.gatling.control.UserFeeder

object CommonSteps {

  private val loopVariableName = "any"

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
      .pause(1 second)

  def provisionUsersWithMessages(users: Seq[Future[User]]) =
    scenario("ProvisionUserWithMessages")
      .exec(provisionSystemMailboxes(users))
      .repeat(10, loopVariableName) {
        exec(JmapMessages.sendMessagesRandomly(users))
          .pause(1 second, 2 seconds)
      }
      .pause(30 second)

  def provisionUsersWithMessageList(users: Seq[Future[User]]) =
    scenario("provisionUsersWithMessageList")
      .exec(provisionUsersWithMessages(users))
      .exec(JmapMessages.listMessages())
      .pause(1 second)
}
