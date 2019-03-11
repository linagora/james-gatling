package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.core.session.Session
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import org.apache.james.gatling.control.User
import org.apache.james.gatling.utils.RetryAuthentication._
import org.apache.james.gatling.utils.{JmapChecks, RandomStringGenerator}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration.Inf
import scala.util.Random

case class MessageId(id: String = RandomStringGenerator.randomString)
case class RecipientAddress(address: String)
case class Subject(subject: String = RandomStringGenerator.randomString)
case class TextBody(text: String = RandomStringGenerator.randomString)

case class RequestTitle(title: String)
case class Property(name: String)

object JmapMessages {

  private val messageIdsPath = "$[0][1].messageIds[*]"
  
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
  
  def retrieveSentMessageIds() = {
    JmapAuthentication.authenticatedQuery("retrieveMessageIds", "/jmap")
      .body(StringBody(
        s"""[[
          "getMessageList",
          {
            "filter": {
              "inMailboxes" : [ "$${sentMailboxId}" ]
            }
          },
          "#0"
          ]]"""))
      .check(saveMessageIds: _*)
  }

  def saveMessageIds: Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError,
    jsonPath(messageIdsPath).count.gt(0),
    jsonPath(messageIdsPath).findAll.saveAs("messageIds"))

  def moveMessagesToMailboxId =
    exec((session: Session) => session.set("update", {
        session("messageIds").as[Vector[String]].map(x =>s""" 
            "$x" : { "mailboxIds": [ "${session("mailboxId").as[Vector[String]].head}" ] }"""
          )
          .mkString(",")
      }))
    .exec(JmapAuthentication.authenticatedQuery("moveSentMessagesToMessageId", "/jmap")
            .body(StringBody(
              s"""[[
                "setMessages",
                {
                  "update": { $${update} }
                },
                "#0"
                ]]"""))
            .check(hasBeenUpdated))

  def hasBeenUpdated = 
    jsonPath("$..updated[*]").count.gt(0)
        
  def sendMessagesChecks(messageId: MessageId): Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError,
    JmapChecks.created(messageId))

  def sendMessagesWithRetryAuthentication(messageId: MessageId, recipientAddress: RecipientAddress, subject: Subject, textBody: TextBody) =
    execWithRetryAuthentication(sendMessages(messageId, recipientAddress, subject, textBody), sendMessagesChecks(messageId))


  def sendMessagesRandomlyWithRetryAuthentication(users: Seq[Future[User]]) =
    sendMessagesWithRetryAuthentication(
      messageId = MessageId(),
      recipientAddress = selectRecipientAtRandom(users),
      subject = Subject(),
      textBody = TextBody())

  def selectRecipientAtRandom(users: Seq[Future[User]]) =
    RecipientAddress(
      Await.result(users(Random.nextInt(users.length)), Inf).username.value)

  def listMessages() =
    JmapAuthentication.authenticatedQuery("listMessages", "/jmap")
      .body(StringBody(
        """[[
          "getMessageList", { },
          "#0"
          ]]"""))

  val listMessagesChecks: Seq[HttpCheck] = List(
      status.is(200),
      JmapChecks.noError,
      jsonPath("$[0][1].messageIds[*]").findAll.saveAs("messageIds"))

  def listMessagesWithRetryAuthentication() =
    execWithRetryAuthentication(listMessages, listMessagesChecks)

  def getMessagesWithRetryAuthentication() =
    execWithRetryAuthentication(getRandomMessage(), getRandomMessageChecks)

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

  val getRandomMessageChecks: Seq[HttpCheck] = List(
      status.is(200),
      JmapChecks.noError)

  def markAsRead() = performUpdateWithRetryAuthentication(RequestTitle("markAsRead"), Property("isUnread"), value = false)
  def markAsAnswered() = performUpdateWithRetryAuthentication(RequestTitle("markAsAnswered"), Property("isAnswered"), value = true)
  def markAsFlagged() = performUpdateWithRetryAuthentication(RequestTitle("markAsFlagged"), Property("isFlagged"), value = true)

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
  }

  val performUpdateChecks: Seq[HttpCheck] = List(
      status.is(200),
      JmapChecks.noError)

  def performUpdateWithRetryAuthentication(title: RequestTitle, property: Property, value: Boolean) = 
    execWithRetryAuthentication(performUpdate(title, property, value), performUpdateChecks)

}
