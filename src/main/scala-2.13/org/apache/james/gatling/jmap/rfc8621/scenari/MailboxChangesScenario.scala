package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.rfc8621.JmapEmail.{nonEmptyListMessagesChecks, openpaasEmailQueryParameters, queryEmails}
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.scenari.MailboxChangesScenario.{emailIds, inbox, newState, oldState}
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapMailbox, SessionStep}

import scala.concurrent.duration.{Duration, _}

private object MailboxChangesScenario {
  val oldState = "oldState"
  val newState = "newState"
  val emailIds = "emailIds"
  val inbox = "inboxID"
}

class MailboxChangesScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("MailboxChangesScenario")
      .feed(userFeeder)
      .exec(SessionStep.retrieveAccountId)
      .during(duration.toSeconds.toInt) {
        exec(JmapMailbox.getMailboxes
          .check(statusOk, noError, JmapMailbox.saveStateAs(oldState), JmapMailbox.saveInboxAs(inbox)))
        .exec(queryEmails(queryParameters = openpaasEmailQueryParameters(inbox))
          .check(statusOk, noError, nonEmptyListMessagesChecks(emailIds)))
        .exec(JmapEmail.markAsSeen()
          .check(statusOk, noError))
        .exec(JmapMailbox.getNewState()
          .check(statusOk, noError, JmapMailbox.saveNewStateAs(newState)))
        .pause(1 second)
      }
}