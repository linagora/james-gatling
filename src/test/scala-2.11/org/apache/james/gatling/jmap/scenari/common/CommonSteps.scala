package org.apache.james.gatling.jmap.scenari.common

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeeder
import org.apache.james.gatling.jmap.{JmapAuthentication, JmapMailboxes, JmapMessages}

import scala.concurrent.duration._

object CommonSteps {

  private val loopVariableName = "any"

  def authentication(feeder: UserFeeder) =
    scenario("JmapAuthentication")
      .feed(feeder)
      .pause(1 second, 30 second)
      .exec(JmapAuthentication.authentication())
      .pause(1 second)

  def provisionSystemMailboxes(feeder: UserFeeder) =
    scenario("provisionSystemMailboxes")
      .exec(authentication(feeder))
      .pause(1 second)
      .exec(JmapMailboxes.getSystemMailboxes)
      .pause(1 second)

  def provisionUsersWithMessages(feeder: UserFeeder) =
    scenario("ProvisionUserWithMessages")
      .exec(provisionSystemMailboxes(feeder))
      .repeat(10, loopVariableName) {
        exec(JmapMessages.sendMessagesRandomly(feeder))
          .pause(1 second, 2 seconds)
      }
      .pause(30 second)

  def provisionUsersWithMessageList(feeder: UserFeeder) =
    scenario("provisionUsersWithMessageList")
      .exec(provisionUsersWithMessages(feeder))
      .exec(JmapMessages.listMessages())
      .pause(1 second)
}
