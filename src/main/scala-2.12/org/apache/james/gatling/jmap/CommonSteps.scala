package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.apache.james.gatling.control.User

import scala.concurrent.Future
import scala.concurrent.duration._

object CommonSteps {

  trait UserPicker {
    def pick(): User
  }

  private val loopVariableName = "any"

  def authentication(): ChainBuilder =
    exec(
      pause(1 second, 5 second)
      .exec(JmapAuthentication.authentication())
      .pause(1 second))

  def provisionSystemMailboxes(): ChainBuilder =
    exec(authentication())
      .pause(1 second)
      .exec(JmapMailboxes.getSystemMailboxesWithRetryAuthentication)
      .pause(1 second)

  def provisionUsersWithMessages(userPicker: UserPicker, numberOfMessages: Int): ChainBuilder =
    exec(provisionSystemMailboxes())
      .repeat(numberOfMessages, loopVariableName) {
        exec(JmapMessages.sendMessagesToUserWithRetryAuthentication(userPicker))
          .pause(1 second, 2 seconds)
      }
      .pause(5 second)

  def provisionUsersWithMailboxesAndMessages(userPicker: UserPicker, numberOfMailboxes: Int, numberOfMessages: Int): ChainBuilder =
    exec(provisionSystemMailboxes())
      .repeat(numberOfMailboxes) {
        provisionNewMailboxAndRememberItsIdAndName()
        .repeat(numberOfMessages) {
          exec(JmapMessages.sendMessagesToUserWithRetryAuthentication(userPicker))
        }
        .pause(1 second, 2 seconds)
        .exec(JmapMessages.retrieveSentMessageIds())
        .exec(JmapMessages.moveMessagesToMailboxId)
      }
      .pause(5 second)


  def provisionNewMailboxAndRememberItsIdAndName(): ChainBuilder =
    exec((session: Session) => session.set("createdId", Id.generate().id))
        .exec((session: Session) => session.set("mailboxName", Name.generate().name))
        .exec(JmapMailboxes.createMailbox())

  def provisionUsersWithMessageList(userPicker: UserPicker, numberOfMessages: Int): ChainBuilder =
    exec(provisionUsersWithMessages(userPicker, numberOfMessages))
      .exec(JmapMessages.listMessagesWithRetryAuthentication())
      .pause(1 second)
}
