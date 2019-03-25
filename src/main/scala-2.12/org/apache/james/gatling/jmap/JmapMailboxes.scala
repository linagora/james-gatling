package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import org.apache.james.gatling.utils.JmapChecks
import org.apache.james.gatling.utils.RetryAuthentication.execWithRetryAuthentication
import org.apache.james.gatling.utils.RandomStringGenerator
import fabricator.Words

object Id {
  def generate(): Id =
    Id(RandomStringGenerator.randomString)
}
case class Id private(id: String) extends AnyVal

object Name {
  private val words = Words()
  
  def generate(): Name =
    Name(words.words(2).mkString("_"))
}
case class Name private(name: String) extends AnyVal

object JmapMailboxes {

  private val mailboxListPath = "[0][1].list"
  private val inboxIdPath = s"$$$mailboxListPath[?(@.role == 'inbox')].id"
  private val outboxIdPath = s"$$$mailboxListPath[?(@.role == 'outbox')].id"
  private val sentIdPath = s"$$$mailboxListPath[?(@.role == 'sent')].id"
  private val draftsIdPath = s"$$$mailboxListPath[?(@.role == 'drafts')].id"
  private val trashIdPath = s"$$$mailboxListPath[?(@.role == 'trash')].id"
  private val spamIdPath = s"$$$mailboxListPath[?(@.role == 'spam')].id"
  val numberOfSystemMailboxes = 6

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

  def checkSystemMailboxIdsHaveNotChanged: Seq[HttpCheck] =
    getMailboxesChecks ++ List[HttpCheck] (
      jsonPath(inboxIdPath).is("${inboxMailboxId}"),
      jsonPath(outboxIdPath).is("${outboxMailboxId}"),
      jsonPath(sentIdPath).is("${sentMailboxId}"),
      jsonPath(draftsIdPath).is("${draftMailboxId}"),
      jsonPath(trashIdPath).is("${trashMailboxId}"),
      jsonPath(spamIdPath).is("${spamMailboxId}")
    )

  val getSystemMailboxesChecks: Seq[HttpCheck] = getMailboxesChecks ++ List[HttpCheck](
    jsonPath(inboxIdPath).saveAs("inboxMailboxId"),
    jsonPath(outboxIdPath).saveAs("outboxMailboxId"),
    jsonPath(sentIdPath).saveAs("sentMailboxId"),
    jsonPath(draftsIdPath).saveAs("draftMailboxId"),
    jsonPath(trashIdPath).saveAs("trashMailboxId"),
    jsonPath(spamIdPath).saveAs("spamMailboxId"))

  def storeMailboxIds: Seq[HttpCheck] = getSystemMailboxesChecks

  def getSystemMailboxes = getMailboxes

  def getSystemMailboxesWithRetryAuthentication = execWithRetryAuthentication(getSystemMailboxes, getSystemMailboxesChecks)

  def getSystemMailboxesWithChecks = getSystemMailboxes.check(getSystemMailboxesChecks: _*)
}
