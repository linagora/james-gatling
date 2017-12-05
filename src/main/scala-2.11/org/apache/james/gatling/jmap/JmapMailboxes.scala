package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import org.apache.james.gatling.utils.JmapChecks
import org.apache.james.gatling.utils.RetryAuthentication.execWithRetryAuthentication
import org.apache.james.gatling.utils.RandomStringGenerator

case class Id(id: String = RandomStringGenerator.randomString)
case class Name(name: String = RandomStringGenerator.randomString)

object JmapMailboxes {

  private val mailboxListPath = "[0][1].list"
  private val inboxIdPath = s"$$$mailboxListPath[?(@.role == 'inbox')].id"
  private val outboxIdPath = s"$$$mailboxListPath[?(@.role == 'outbox')].id"
  private val sentIdPath = s"$$$mailboxListPath[?(@.role == 'sent')].id"

  def getMailboxes =
    JmapAuthentication.authenticatedQuery("getMailboxes", "/jmap")
      .body(StringBody("""[["getMailboxes", {}, "#0"]]"""))

  def createMailbox(id: Id, name: Name) =
    JmapAuthentication.authenticatedQuery("setMailboxes", "/jmap")
      .body(StringBody(s"""[["setMailboxes", 
            {
              "create": {
                "${id.id}": {
                  "name": "${name.name}"
                } 
              }
            }, "#0"]]"""))

  val getMailboxesChecks: Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError)

  val getSystemMailboxesChecks: Seq[HttpCheck] = getMailboxesChecks ++ List[HttpCheck](
    jsonPath(inboxIdPath).saveAs("inboxMailboxId"),
    jsonPath(outboxIdPath).saveAs("outboxMailboxId"),
    jsonPath(sentIdPath).saveAs("sentMailboxId"))

  def getSystemMailboxes = getMailboxes

  def getSystemMailboxesWithRetryAuthentication = execWithRetryAuthentication(getSystemMailboxes, getSystemMailboxesChecks)

  def getSystemMailboxesWithChecks = getSystemMailboxes.check(getSystemMailboxesChecks: _*)
}
