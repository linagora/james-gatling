package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.apache.james.gatling.control.User

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
      .exec(JmapMailbox.getSystemMailboxesWithRetryAuthentication)
      .pause(1 second)

  def provisionUsersWithMessages(numberOfMessages: Int): ChainBuilder =
    exec(provisionSystemMailboxes())
      .repeat(numberOfMessages, loopVariableName) {
        exec(JmapMessages.sendMessagesToUserWithRetryAuthentication())
          .pause(1 second, 2 seconds)
      }
      .pause(5 second)

  def provisionUsersWithMailboxesAndMessages(numberOfMailboxes: Int, numberOfMessages: Int): ChainBuilder =
    exec(provisionSystemMailboxes())
      .repeat(numberOfMailboxes) {
        provisionNewMailboxAndRememberItsIdAndName()
        .repeat(numberOfMessages) {
          exec(JmapMessages.sendMessagesToUserWithRetryAuthentication())
        }
        .pause(1 second, 2 seconds)
        .exec(JmapMessages.retrieveSentMessageIds())
        .exec(JmapMessages.moveMessagesToMailboxId)
      }
      .pause(5 second)


  def provisionNewMailboxAndRememberItsIdAndName(): ChainBuilder =
    exec((session: Session) => session.set("createdId", MailboxId.generate().id))
        .exec((session: Session) => session.set("mailboxName", MailboxName.generate().name))
        .exec(JmapMailbox.createMailbox())

  def provisionUsersWithMessageList(numberOfMessages: Int): ChainBuilder =
    exec(provisionUsersWithMessages(numberOfMessages))
      .exec(JmapMessages.listMessagesWithRetryAuthentication())
      .pause(1 second)
}
