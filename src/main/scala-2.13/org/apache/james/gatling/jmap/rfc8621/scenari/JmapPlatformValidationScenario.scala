package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef.{exec, _}
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.rfc8621.JmapEmail.{nonEmptyListMessagesChecks, openpaasEmailQueryParameters, queryEmails, saveOneEmail}
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.scenari.JmapPlatformValidationScenario.{accountId, draft, emailId, emailIds, emailState, inbox, mailboxState, messageIds, outbox, randomMailbox, spam}
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapMailbox, SessionStep}

import scala.concurrent.duration._

object JmapPlatformValidationScenario {
  val mailboxState = "mailboxState"
  val emailState = "emailState"
  val inbox = "inboxID"
  val draft = "draftMailboxId"
  val spam = "spamMailboxId"
  val outbox = "outboxMailboxId"
  val accountId = "accountId"
  val emailIds = "emailIds"
  val emailId = "emailId"
  val messageIds = "messageIds"
  val randomMailbox = "randomMailbox"
}

class JmapPlatformValidationScenario (minMessagesInMailbox: Int,
                                      minWaitDelay: FiniteDuration = 20 seconds,
                                      maxWaitDelay: FiniteDuration = 40 seconds) {
  val flagUpdate: ChainBuilder = randomSwitch(
    70.0 -> exec(JmapEmail.markAsSeen()),
    20.0 -> exec(JmapEmail.markAsAnswered()),
    10.0 -> exec(JmapEmail.markAsFlagged()))
    .asInstanceOf[ChainBuilder]

  val getNewState: ChainBuilder =
    exec(JmapMailbox.getNewState(accountId, mailboxState))
      .pause(1 second)
      .exec(JmapEmail.getNewState(accountId, emailState))

  val inboxHomeLoading: InboxLoadingScenario = new InboxLoadingScenario()
  val openArbitrary: OpenEmailScenario = new OpenEmailScenario
  val selectArbitrary: SelectMailboxScenario = new SelectMailboxScenario(minMessagesInMailbox)

  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("PushPlatformValidation")
      .feed(userFeeder)
      .group("prepare")(
        exec(SessionStep.retrieveAccountId)
          .exec(JmapMailbox.getMailboxes
            .check(statusOk, noError,
              JmapMailbox.saveStateAs(mailboxState),
              JmapMailbox.saveInboxAs(inbox),
              JmapMailbox.saveDraftAs(draft),
              JmapMailbox.saveDraftAs(spam),
              JmapMailbox.saveOutboxAs(outbox),
              JmapMailbox.saveRandomMailboxWithAtLeastMessagesAs(randomMailbox, minMessagesInMailbox)))
          .exec(queryEmails(queryParameters = openpaasEmailQueryParameters(inbox))
            .check(statusOk, noError,
              saveOneEmail(emailId),
              nonEmptyListMessagesChecks(emailIds),
              nonEmptyListMessagesChecks(messageIds)))
          .exec(JmapEmail.getState()
            .check(statusOk, noError, JmapEmail.saveStateAs(emailState)))
          .exec(queryEmails(queryParameters = openpaasEmailQueryParameters(inbox))))
      .during(duration.toSeconds.toInt) {
        exec(randomSwitch(
          2.0 -> inboxHomeLoading.inboxHomeLoading,
          2.0 -> JmapEmail.queryEmailsAndCheck(JmapEmail.filterKeywordQueryParameter()),
          3.0 -> exec(JmapEmail.performMove()),
          5.0 -> JmapEmail.submitEmails(recipientFeeder),
          8.0 -> selectArbitrary.selectArbitrary,
          25.0 -> openArbitrary.openArbitrary,
          10.0 -> flagUpdate,
          15.0 -> getNewState,
          30.0 -> exec())
          .asInstanceOf[ChainBuilder]
          .pause(minWaitDelay, maxWaitDelay))
      }
}
