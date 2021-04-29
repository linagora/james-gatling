package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef.{exec, _}
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.CommonSteps.provisionSystemMailboxes
import org.apache.james.gatling.jmap.rfc8621.JmapEmail.{nonEmptyListMessagesChecks, openpaasEmailQueryParameters, queryEmails}
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.JmapWebsocket.{enablePush, websocketClose, websocketConnect}
import org.apache.james.gatling.jmap.rfc8621.scenari.EmailChangesScenario.emailIds
import org.apache.james.gatling.jmap.rfc8621.scenari.PushPlatformValidationScenario.{emailState, mailboxState}
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapMailbox, SessionStep}

import scala.concurrent.duration._

object PushPlatformValidationScenario {
  val mailboxState = "mailboxState"
  val emailState = "emailState"
  val inbox = "inboxID"
  val accountId = "accountId"
}

class PushPlatformValidationScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("PushPlatformValidation")
      .feed(userFeeder)

      .group("prepare")(
      exec(SessionStep.retrieveAccountId)
      .exec(JmapMailbox.getMailboxes
        .check(statusOk, noError, JmapMailbox.saveStateAs(mailboxState), JmapMailbox.saveInboxAs(PushPlatformValidationScenario.inbox)))
      .exec(queryEmails(queryParameters = openpaasEmailQueryParameters(PushPlatformValidationScenario.inbox))
        .check(statusOk, noError, nonEmptyListMessagesChecks(emailIds)))
      .exec(JmapEmail.getEmails()
        .check(statusOk, noError, JmapEmail.saveStateAs(emailState)))
      .exec(provisionSystemMailboxes()))
      .exec(websocketConnect().onConnected(
        exec(enablePush)
          .during(duration) {
            randomSwitch(5.0 -> exec(JmapEmail.submitEmails(recipientFeeder)),
              35.0 -> exec(JmapEmail.markAsSeen(emailIds)),
              5.0 -> exec(JmapMailbox.getNewState(PushPlatformValidationScenario.accountId, mailboxState)),
              55.0 -> exec(JmapEmail.getNewState(PushPlatformValidationScenario.accountId, emailState)))
          }))
      .exec(websocketClose())
}

