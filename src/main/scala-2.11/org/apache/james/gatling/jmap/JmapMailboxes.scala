package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.apache.james.gatling.utils.JmapChecks

object JmapMailboxes {

  private val mailboxListPath = "[0][1].list"

  def getMailboxes =
    JmapAuthentication.authenticatedQuery("getMailboxes", "/jmap")
      .body(StringBody("""[["getMailboxes", {}, "#0"]]"""))
      .check(status.is(200))
      .check(JmapChecks.noError)

  def getMailboxIds = {
    val idPaths = s"$$$mailboxListPath[*].id"
    getMailboxes
      .check(jsonPath(idPaths).findAll.saveAs("mailboxIds"))
  }

  def getSystemMailboxes = {
    val inboxIdPath = s"$$$mailboxListPath[?(@.role == 'inbox')].id"
    val outboxIdPath = s"$$$mailboxListPath[?(@.role == 'outbox')].id"
    val sentIdPath = s"$$$mailboxListPath[?(@.role == 'sent')].id"
    getMailboxes
      .check(jsonPath(inboxIdPath).saveAs("inboxMailboxId"))
      .check(jsonPath(outboxIdPath).saveAs("outboxMailboxId"))
      .check(jsonPath(sentIdPath).saveAs("sentMailboxId"))
  }
}
