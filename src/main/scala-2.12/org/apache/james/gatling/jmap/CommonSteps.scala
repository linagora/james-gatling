package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.apache.james.gatling.control.User

import scala.concurrent.Future
import scala.concurrent.duration._

object CommonSteps {

  private val loopVariableName = "any"

  def authentication(): ChainBuilder =
    exec(
      pause(1 second, 30 second)
      .exec(JmapAuthentication.authentication())
      .pause(1 second))

  def provisionSystemMailboxes(): ChainBuilder =
    exec(authentication())
      .pause(1 second)
      .exec(JmapMailboxes.getSystemMailboxesWithRetryAuthentication)
      .pause(1 second)

  def provisionUsersWithMessages(users: Seq[Future[User]], randomlySentMails: Int): ChainBuilder =
    exec(provisionSystemMailboxes())
      .repeat(randomlySentMails, loopVariableName) {
        exec(JmapMessages.sendMessagesRandomlyWithRetryAuthentication(users))
          .pause(1 second, 2 seconds)
      }
      .pause(30 second)

  def provisionUsersWithMailboxesAndMessages(users: Seq[Future[User]], numberOfMailboxes: Int, numberOfMessages: Int): ChainBuilder =
    exec(provisionSystemMailboxes())
      .repeat(numberOfMailboxes) {
        provisionNewMailboxAndRememberItsIdAndName()
        .repeat(numberOfMessages) {
          exec(JmapMessages.sendMessagesRandomlyWithRetryAuthentication(users))
        }
        .pause(1 second, 2 seconds)
        .exec(JmapMessages.retrieveSentMessageIds())
        .exec(JmapMessages.moveMessagesToMailboxId)
      }
      .pause(30 second)


  def provisionNewMailboxAndRememberItsIdAndName(): ChainBuilder =
    exec((session: Session) => session.set("createdId", Id.generate().id))
        .exec((session: Session) => session.set("mailboxName", Name.generate().name))
        .exec(JmapMailboxes.createMailbox())

  def provisionUsersWithMessageList(users: Seq[Future[User]], randomlySentMails: Int): ChainBuilder =
    exec(provisionUsersWithMessages(users, randomlySentMails))
      .exec(JmapMessages.listMessagesWithRetryAuthentication())
      .pause(1 second)
}
