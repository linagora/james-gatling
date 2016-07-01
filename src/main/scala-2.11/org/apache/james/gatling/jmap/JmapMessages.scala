package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.utils.{JmapChecks, RandomStringGenerator}

import scala.util.Random

case class MessageId(id: String = RandomStringGenerator.randomString)
case class RecipientAddress(address: String)
case class Subject(subject: String = RandomStringGenerator.randomString)
case class TextBody(text: String = RandomStringGenerator.randomString)

case class RequestTitle(title: String)
case class Property(name: String)

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

  def sendMessagesRandomly(feeder: Array[Map[String, String]]) =
    sendMessages(
      messageId = MessageId(),
      recipientAddress = selectectRecipientAtRandom(feeder),
      subject = Subject(),
      textBody = TextBody())

  def selectectRecipientAtRandom(feeder: Array[Map[String, String]]) =
    RecipientAddress(
      feeder(Random.nextInt(feeder.length))
        .get(UserFeeder.USERNAME_SESSION_PARAM)
        .get)

  def listMessages() =
    JmapAuthentication.authenticatedQuery("listMessages", "/jmap")
      .body(StringBody(
        """[[
          "getMessageList", { },
          "#0"
          ]]"""))
      .check(status.is(200))
      .check(JmapChecks.noError)
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
      .check(JmapChecks.noError)

  def markAsRead() = performUpdate(RequestTitle("markAsRead"), Property("isUnread"), value = false)
  def markAsAnswered() = performUpdate(RequestTitle("markAsAnswered"), Property("isAnswered"), value = true)
  def markAsFlagged() = performUpdate(RequestTitle("markAsFlagged"), Property("isFlagged"), value = true)

  def performUpdate(title: RequestTitle, property: Property, value: Boolean) = {
    JmapAuthentication.authenticatedQuery(title.title, "/jmap")
      .body(StringBody(
        s"""[[
          "setMessages",
          {
            "update": {
              "$${messageIds.random()}" : {
                "${property.name}": "$value"
              }
            }
          },
          "#0"
          ]]"""))
      .check(status.is(200))
      .check(JmapChecks.noError)
  }

}
