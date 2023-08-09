package org.apache.james.gatling.imap

import org.apache.james.gatling.Fixture
import org.apache.james.gatling.imap.scenari.ImapListMessagesBodyStructureScenario
import org.apache.james.gatling.jmap.draft.MailboxName

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


class ImapListMessagesBodyStructureIT extends ImapIT {
  private val MAILS_FETCHED = 1
  private val mailboxes = List(MailboxName("INBOX"), MailboxName("rmbx0"), MailboxName("rmbx1.smbx5"), MailboxName("rmbx9"))

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
    val mailboxCreations = users.flatMap(user => mailboxes.map(server.createMailbox(user.username)))
    Await.result(Future.sequence(mailboxCreations), 30 seconds)
    users.flatMap(user => mailboxes.map(server.sendMessage(user)))
  }

  scenario(feederBuilder => {
    new ImapListMessagesBodyStructureScenario().generate(feederBuilder, mailboxes, MAILS_FETCHED)
  })
}
