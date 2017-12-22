package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import org.apache.james.gatling.utils.JmapChecks
import org.apache.james.gatling.utils.RetryAuthentication.execWithRetryAuthentication
import org.apache.james.gatling.utils.RandomStringGenerator
import fabricator.Words
import io.gatling.core.session.Session

object Id {
  def generate(): Id =
    Id(RandomStringGenerator.randomString)
}
case class Id private(id: String)

object Name {
  private val words = Words()
  
  def generate(): Name =
    Name(words.words(2).mkString("_"))
}
case class Name private(name: String)

object JmapMailboxes {

  private val mailboxListPath = "[0][1].list"
  private val inboxIdPath = s"$$$mailboxListPath[?(@.role == 'inbox')].id"
  private val outboxIdPath = s"$$$mailboxListPath[?(@.role == 'outbox')].id"
  private val sentIdPath = s"$$$mailboxListPath[?(@.role == 'sent')].id"
  val numberOfSystemMailboxes = 5

  def getMailboxes =
    JmapAuthentication.authenticatedQuery("getMailboxes", "/jmap")
      .body(StringBody("""[["getMailboxes", {}, "#0"]]"""))

  def createMailbox() =
    JmapAuthentication.authenticatedQuery("setMailboxes", "/jmap")
      .body(StringBody(s"""[["setMailboxes", 
            {
              "create": {
                "$${createdId}": {
                  "name": "$${mailboxName}"
                } 
              }
            }, "#0"]]"""))
      .check(saveMailboxId())

  def saveMailboxId() = {
    jsonPath(s"""$$[0][1].created..$${createdId}.id""").findAll.saveAs("mailboxId")
  }

  val getMailboxesChecks: Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError)

  def assertNumberOfMailboxes(numberOfMailboxes: Int): HttpCheck =
    jsonPath(s"$$$mailboxListPath[*].id").count.is(numberOfMailboxes)
    
  def getMailboxesChecks(expectedNumberOfMailboxes: Int): Seq[HttpCheck] =
    getMailboxesChecks :+ assertNumberOfMailboxes(expectedNumberOfMailboxes)

  def checkMailboxIdsHasNotChange: Seq[HttpCheck] =
    getMailboxesChecks :+ assertMailboxIdsHasNotChange

  def assertMailboxIdsHasNotChange: HttpCheck =
    exec((session: Session) => session.set("mailboxIdsAsString", {
        session("mailboxIds").as[Vector[String]].map(x =>s"$x")
          .mkString(",")
      })).
//      .check(jsonPath(s"""$$$mailboxListPath[*].id).in($${mailboxIdsAsString}"""))
    
  val getSystemMailboxesChecks: Seq[HttpCheck] = getMailboxesChecks ++ List[HttpCheck](
    jsonPath(inboxIdPath).saveAs("inboxMailboxId"),
    jsonPath(outboxIdPath).saveAs("outboxMailboxId"),
    jsonPath(sentIdPath).saveAs("sentMailboxId"))

  def storeMailboxIds: Seq[HttpCheck] = getMailboxesChecks ++ List[HttpCheck](
    jsonPath(s"$$$mailboxListPath[*].id").findAll.saveAs("mailboxIds"))

  def getSystemMailboxes = getMailboxes

  def getSystemMailboxesWithRetryAuthentication = execWithRetryAuthentication(getSystemMailboxes, getSystemMailboxesChecks)

  def getSystemMailboxesWithChecks = getSystemMailboxes.check(getSystemMailboxesChecks: _*)
}
