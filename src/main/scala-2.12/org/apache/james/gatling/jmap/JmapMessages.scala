package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.json.Json
import io.gatling.core.session.Session
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import org.apache.james.gatling.control.{RecipientFeeder, User, UserFeeder}
import org.apache.james.gatling.jmap.RetryAuthentication._
import org.apache.james.gatling.utils.RandomStringGenerator

case class MessageId(id: String = RandomStringGenerator.randomString) extends AnyVal
case class RecipientAddress(address: String) extends AnyVal
case class Subject(subject: String = RandomStringGenerator.randomString) extends AnyVal
case class TextBody(text: String = RandomStringGenerator.randomString) extends AnyVal

case class RequestTitle(title: String) extends AnyVal
case class Property(name: String) extends AnyVal

object RecipientAddress {
  def apply(user: User): RecipientAddress =
    RecipientAddress(user.username.value)
}

object JmapMessages {

  private val messageIdsPath = "$[0][1].messageIds[*]"

  type JmapParameters = Map[String, Any]
  val NO_PARAMETERS : JmapParameters = Map()

  val MessageIdSessionParam = "messageId"
  val SubjectSessionParam = "subject"
  val TextBodySessionParam = "textBody"

  def sendMessages() =
    JmapAuthentication.authenticatedQuery("sendMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "setMessages",
          {
            "create": {
              "$${$MessageIdSessionParam}" : {
                "from": {"name":"$${${UserFeeder.UsernameSessionParam}}", "email": "$${${UserFeeder.UsernameSessionParam}}"},
                "to":  [{"name":"$${${RecipientFeeder.RecipientSessionParam}}", "email": "$${${RecipientFeeder.RecipientSessionParam}}"}],
                "textBody": "$${$TextBodySessionParam}",
                "subject": "$${$SubjectSessionParam}",
                "mailboxIds": ["$${${JmapMailbox.OutboxMailboxIdSessionParam}}"]
              }
            }
          },
          "#0"
          ]]"""))

  def retrieveSentMessageIds() = {
    JmapAuthentication.authenticatedQuery("retrieveMessageIds", "/jmap")
      .body(StringBody(
        """[[
          "getMessageList",
          {
            "filter": {
              "inMailboxes" : [ "${sentMailboxId}" ]
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
              """[[
                "setMessages",
                {
                  "update": { ${update} }
                },
                "#0"
                ]]"""))
            .check(hasBeenUpdated))

  def hasBeenUpdated =
    jsonPath("$..updated[*]").count.gt(0)

  def sendMessagesChecks(): Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError,
    JmapChecks.created())

  def sendMessagesWithRetryAuthentication() = {
    execWithRetryAuthentication(sendMessages(), sendMessagesChecks())
  }

  def sendMessagesToUserWithRetryAuthentication(recipientFeeder: FeederBuilder) = {
    val mailFeeder = Iterator.continually(
      Map(
        MessageIdSessionParam -> MessageId().id,
        SubjectSessionParam -> Subject().subject,
        TextBodySessionParam -> TextBody().text
      )
    )
    feed(mailFeeder)
      .feed(recipientFeeder)
      .exec(sendMessagesWithRetryAuthentication())
  }

  def openpaasListMessageFilter(mailboxesKey: List[String]): JmapParameters = {
    val mailboxes = mailboxesKey.map(key => s"$${$key}")
    Map(
      "inMailboxes" -> mailboxes,
      "text" -> null)
  }

  def openpaasListMessageParameters(mailboxKey: String = "inboxID"): JmapParameters =
    openpaasListMessageParameters(List(mailboxKey))


  def openpaasListMessageParameters(mailboxesKey: List[String]): JmapParameters =
    Map("filter" -> openpaasListMessageFilter(mailboxesKey),
      "sort" -> Seq("date desc"),
      "collapseThreads" -> false,
      "fetchMessages" -> false,
      "position" -> 0,
      "limit" -> 30
    )

  def listMessages(queryParameters: JmapParameters = NO_PARAMETERS) =
    JmapAuthentication.authenticatedQuery("listMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "getMessageList",
            ${Json.stringify(queryParameters)}
           ,
          "#0"
          ]]"""))

  val listMessagesChecks: Seq[HttpCheck] = List(
      status.is(200),
      JmapChecks.noError,
      jsonPath("$[0][1].messageIds[*]").findAll.saveAs("messageIds"))

  def listMessagesWithRetryAuthentication() =
    execWithRetryAuthentication(listMessages(), listMessagesChecks)

  def getMessagesWithRetryAuthentication() =
    execWithRetryAuthentication(getRandomMessage(), getRandomMessageChecks)

  val typicalMessageProperties: List[String] = List("bcc", "cc", "date", "from", "hasAttachment", "htmlBody", "id", "isAnswered", "isDraft", "isFlagged", "isUnread", "mailboxIds", "size", "subject", "textBody", "to")

  val previewMessageProperties: List[String] = List("bcc", "blobId", "cc", "date", "from", "hasAttachment", "headers", "id", "isAnswered", "isDraft", "isFlagged", "isForwarded", "isUnread", "mailboxIds", "preview", "replyTo", "subject", "threadId", "to")

  val openpaasInboxOpenMessageProperties: List[String] = List("attachments", "bcc", "blobId", "cc", "date", "from", "hasAttachment", "headers", "htmlBody", "id", "isDraft", "isFlagged", "isUnread", "mailboxIds", "preview", "replyTo", "subject", "textBody", "threadId", "to")





  def getRandomMessage(properties: List[String] = typicalMessageProperties, messageIdsKey: String = "messageIds") =
    JmapAuthentication.authenticatedQuery("getMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "getMessages",
          {
            "ids": ["$${$messageIdsKey.random()}"],
            "properties": [ ${Json.stringify(properties)} ]
          },
          "#0"
          ]]"""))


  def getMessages(properties: List[String], messageIdsKey: String = "messageIds") =
    JmapAuthentication.authenticatedQuery("getMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "getMessages",
          {
            "ids": $${$messageIdsKey.jsonStringify()},
            "properties": [ ${Json.stringify(properties)} ]
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
