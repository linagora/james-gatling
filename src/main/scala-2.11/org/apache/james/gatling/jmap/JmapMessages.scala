package org.apache.james.gatling.jmap

import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.utils.{JmapChecks, RandomStringGenerator}

import scala.util.Random
import org.apache.james.gatling.control.{User, Username}
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

case class MessageId(id: String = RandomStringGenerator.randomString)
case class RecipientAddress(address: String)
case class Subject(subject: String = RandomStringGenerator.randomString)
case class TextBody(text: String = RandomStringGenerator.randomString)

object JmapMessages {

  def sendMessages(messageId: MessageId, recipientAddress: RecipientAddress, subject: Subject, textBody: TextBody) =
    JmapAuthentication.authenticatedQuery("sendMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "setMessages",
          {
            "create": {
              "${messageId.id}" : {
                "from": {"name":"$${username}", "email": "$${username}"},
                "to":  [{"name":"${recipientAddress.address}", "email": "${recipientAddress.address}"}],
                "textBody": "${textBody.text}",
                "subject": "${subject.subject}",
                "mailboxIds": ["$${outboxMailboxId}"]
              }
            }
          },
          "#0"
          ]]"""))
      .check(status.is(200))
      .check(JmapChecks.noError)
      .check(JmapChecks.created(messageId))

  def sendMessagesRandomly(users: Seq[Future[User]]) =
    sendMessages(
      messageId = MessageId(),
      recipientAddress = selectRecipientAtRandom(users),
      subject = Subject(),
      textBody = TextBody())

  def selectRecipientAtRandom(users: Seq[Future[User]]) =
    RecipientAddress(
      Await.result(users(Random.nextInt(users.length)), Inf).username.value)

}
