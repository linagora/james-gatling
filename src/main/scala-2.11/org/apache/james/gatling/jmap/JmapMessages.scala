package org.apache.james.gatling.jmap

import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder

import scala.util.Random

case class MessageId(id: String)
case class RecipientAddress(address: String)
case class Subject(subject: String)
case class TextBody(text: String)

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
      .check(jsonPath("$.error").notExists)

  def sendMessagesRandomly(feeder: Array[Map[String, String]]) =
    sendMessages(
      MessageId(UUID.randomUUID().toString),
      selectectRecipientAtRandom(feeder),
      Subject(UUID.randomUUID().toString),
      TextBody(UUID.randomUUID().toString))

  def selectectRecipientAtRandom(feeder: Array[Map[String, String]]) =
    RecipientAddress(
      feeder(Math.abs(Random.nextInt() % feeder.length))
        .get(UserFeeder.USERNAME)
        .get)

  def listMessages() =
    JmapAuthentication.authenticatedQuery("listMessages", "/jmap")
      .body(StringBody(
        """[[
          "getMessageList", { },
          "#0"
          ]]"""))
      .check(status.is(200))
      .check(jsonPath("$.error").notExists)
      .check(jsonPath("$[0][1].messageIds[*]").findAll.saveAs("messageIds"))

  def getRandomMessage() =
    JmapAuthentication.authenticatedQuery("getMessages", "/jmap")
      .body(StringBody(
        """[[
          "getMessages",
          {
            "ids": ["${messageIds.random()}"],
            "properties": [
              "id",
              "mailboxIds",
              "isUnread",
              "isFlagged",
              "isAnswered",
              "isDraft",
              "hasAttachment",
              "from",
              "to",
              "cc",
              "bcc",
              "subject",
              "date",
              "size",
              "textBody",
              "htmlBody"
            ]
          },
          "#0"
          ]]"""))
      .check(status.is(200))
      .check(jsonPath("$.error").notExists)

}
