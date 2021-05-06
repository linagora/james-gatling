package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef.{exec, _}
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.OpenMessage
import org.apache.james.gatling.jmap.draft.scenari.{JmapInboxHomeLoadingScenario, JmapOpenArbitraryMessageScenario, JmapSelectArbitraryMailboxScenario}
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapMessages}
import org.apache.james.gatling.jmap.rfc8621.JmapEmail.{nonEmptyListMessagesChecks, openpaasEmailQueryParameters, queryEmails}
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.JmapWebsocket.{enablePush, websocketClose, websocketConnect}
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapMailbox, SessionStep}
import org.apache.james.gatling.jmap.scenari.PushPlatformValidationScenario.{draft, emailIds, emailState, inbox, mailboxState, outbox}

import scala.concurrent.duration._

object PushPlatformValidationScenario {
  val mailboxState = "mailboxState"
  val emailState = "emailState"
  val inbox = "inboxID"
  val draft = "draftMailboxId"
  val outbox = "outboxMailboxId"
  val accountId = "accountId"
  val emailIds = "emailIds"
}

class PushPlatformValidationScenario(minMessagesInMailbox: Int) {
  val flagUpdate: ChainBuilder = randomSwitch(
    70.0 -> exec(JmapEmail.markAsSeen()),
    20.0 -> exec(JmapEmail.markAsAnswered()),
    10.0 -> exec(JmapEmail.markAsFlagged()))

  val inboxHomeLoading: JmapInboxHomeLoadingScenario = new JmapInboxHomeLoadingScenario
  val openArbitrary: JmapOpenArbitraryMessageScenario = new JmapOpenArbitraryMessageScenario
  val selectArbitrary: JmapSelectArbitraryMailboxScenario = new JmapSelectArbitraryMailboxScenario(minMessagesInMailbox)

  def sendMessage(recipientFeeder: RecipientFeederBuilder): ChainBuilder = JmapMessages.sendMessagesToUserWithRetryAuthentication(recipientFeeder)

  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("PushPlatformValidation")
      .feed(userFeeder)

      .group("prepare")(
        exec(SessionStep.retrieveAccountId)
        .exec(JmapMailbox.getMailboxes
          .check(statusOk, noError, JmapMailbox.saveStateAs(mailboxState), JmapMailbox.saveInboxAs(inbox), JmapMailbox.saveDraftAs(draft), JmapMailbox.saveOutboxAs(outbox)))
        .exec(queryEmails(queryParameters = openpaasEmailQueryParameters(inbox))
          .check(statusOk, noError, nonEmptyListMessagesChecks(emailIds)))
        .exec(JmapEmail.getEmails()
          .check(statusOk, noError, JmapEmail.saveStateAs(emailState)))
        .exec(CommonSteps.authentication())
        .exec(openArbitrary.prepare)
        .exec(selectArbitrary.prepare))
      .exec(websocketConnect().onConnected(
        exec(enablePush)
          .during(duration) {
            randomSwitch(
              2.0 -> inboxHomeLoading.inboxHomeLoading,
              8.0 -> selectArbitrary.selectArbitrary,
              5.0 -> sendMessage(recipientFeeder),
              30.0 -> openArbitrary.openArbitrary,
              10.0 -> flagUpdate,
              15.0 -> exec(JmapMailbox.getNewState(PushPlatformValidationScenario.accountId, mailboxState))
                  .pause(1 second)
                .exec(JmapEmail.getNewState(PushPlatformValidationScenario.accountId, emailState)),
              30.0 -> exec())
          }))
      .exec(websocketClose())
}

