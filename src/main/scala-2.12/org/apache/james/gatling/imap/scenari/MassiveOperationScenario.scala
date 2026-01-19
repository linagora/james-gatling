package org.apache.james.gatling.imap.scenari

import java.util.Calendar

import com.linagora.gatling.imap.PreDef.{imap, ok}
import com.linagora.gatling.imap.protocol.command.MessageRange.From
import com.linagora.gatling.imap.protocol.command.{MessageRanges, Silent, StoreFlags}
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure._

import scala.collection.immutable.Seq
import scala.concurrent.duration._

class MassiveOperationScenario {
  private val gracePeriod = 5 milliseconds
  private val numberOfMailInInbox = 20000
  private val numberOfSubMailboxes = 1000
  private val mailboxAName = "MailboxA"
  private val mailboxBName = "MailboxB"
  private val mailboxCName = "MailboxC"
  private val mailboxDName = "MailboxD"
  private val mailboxDNewName = "Dbis"

  private val createMailboxA_B_C = exec(imap("createFolder").createFolder(mailboxAName).check(ok))
    .exec(imap("createFolder").createFolder(mailboxBName).check(ok))
    .exec(imap("createFolder").createFolder(mailboxCName).check(ok))
  private val populateMailboxA = repeat(numberOfMailInInbox)(pause(gracePeriod)
    .exec(imap("append").append(mailboxAName, Option.empty[Seq[String]], Option.empty[Calendar],
      """From: expeditor@example.com
        |To: recipient@example.com
        |Subject: test subject
        |
        |Test content
        |abcdefghijklmnopqrstuvwxyz
        |0123456789""".stripMargin).check(ok)))
  private val setAllMessagesInMailboxASeenFlag = imap("storeAll").store(MessageRanges(From(1L)), StoreFlags.add(Silent.Enable(), "\\Seen")).check(ok)
  private val copyAllMessagesToMailboxB = imap("copy").copyMessage(MessageRanges(From(1L)), mailboxBName).check(ok)
  private val moveAllMessagesToMailboxC = imap("move").moveMessage(MessageRanges(From(1L)), mailboxCName).check(ok)
  private val setAllMessagesInMailboxBDeletedFlagAndExpunge = exec(imap("storeAll").store(MessageRanges(From(1L)), StoreFlags.add(Silent.Enable(), "\\Deleted")).check(ok))
    .exec(imap("expunge").expunge().check(ok))
  private val deleteMailboxA_B_C = exec(imap("deleteFolder").deleteFolder(mailboxAName).check(ok))
    .exec(imap("deleteFolder").deleteFolder(mailboxBName).check(ok))
    .exec(imap("deleteFolder").deleteFolder(mailboxCName).check(ok))
  private val createMailboxDWith1000SubMailboxes = exec(imap("createFolder").createFolder(mailboxDName).check(ok))
    .exec(repeat(numberOfSubMailboxes, "loopId")(pause(gracePeriod).exec(exec(imap("createFolder").createFolder(s"$mailboxDName.$${loopId}").check(ok)))))
  private val renameMailboxD = exec(imap("renameFolder").renameFolder(mailboxDName, mailboxDNewName).check(ok))
  private val deleteMailboxD = exec(imap("deleteFolder").deleteFolder(mailboxDNewName).check(ok))
    .exec(repeat(numberOfSubMailboxes, "loopId")(pause(gracePeriod).exec(exec(imap("deleteFolder").deleteFolder(s"$mailboxDNewName.$${loopId}").check(ok)))))


  def generate(feeder: FeederBuilder): ScenarioBuilder =
    scenario("MassiveOperationSupport")
      .feed(feeder)
      .pause(1 second)
      .exec(imap("Connect").connect()).exitHereIfFailed
      .exec(imap("login").login("${username}", "${password}").check(ok))
      .exec(createMailboxA_B_C)
      .exec(imap("select").select(mailboxAName).check(ok))
      .exec(populateMailboxA)
      .pause(2 second)
      .exec(setAllMessagesInMailboxASeenFlag)
      .pause(2 second)
      .exec(copyAllMessagesToMailboxB)
      .pause(2 second)
      .exec(moveAllMessagesToMailboxC)
      .pause(2 seconds)
      .exec(imap("select").select(mailboxBName).check(ok))
      .exec(setAllMessagesInMailboxBDeletedFlagAndExpunge)
      .pause(2 seconds)
      .exec(deleteMailboxA_B_C)
      .exec(createMailboxDWith1000SubMailboxes)
      .pause(1 seconds)
      .exec(renameMailboxD)
      .pause(5 seconds)
      .exec(deleteMailboxD)

}
