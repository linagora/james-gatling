package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.apache.james.gatling.utils.JmapChecks
import io.gatling.http.check.HttpCheck

object JmapMailboxes {

  private val mailboxListPath = "[0][1].list"
  private val inboxIdPath = s"$$$mailboxListPath[?(@.role == 'inbox')].id"
  private val outboxIdPath = s"$$$mailboxListPath[?(@.role == 'outbox')].id"
  private val sentIdPath = s"$$$mailboxListPath[?(@.role == 'sent')].id"

  def getMailboxes =
    JmapAuthentication.authenticatedQuery("getMailboxes", "/jmap")
      .body(StringBody("""[["getMailboxes", {}, "#0"]]"""))

  val getMailboxesChecks: Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError)

  def getSystemMailboxes = getMailboxes

  val getSystemMailboxesChecks: Seq[HttpCheck] = getMailboxesChecks ++ List[HttpCheck](
    jsonPath(inboxIdPath).saveAs("inboxMailboxId"),
    jsonPath(outboxIdPath).saveAs("outboxMailboxId"),
    jsonPath(sentIdPath).saveAs("sentMailboxId"))
}
