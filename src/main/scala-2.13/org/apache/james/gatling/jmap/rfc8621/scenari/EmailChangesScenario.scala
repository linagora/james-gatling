package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef.{exec, scenario, _}
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.rfc8621.JmapEmail.{nonEmptyListMessagesChecks, openpaasEmailQueryParameters, queryEmails}
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.scenari.EmailChangesScenario.{emailIds, inbox, newState, oldState}
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapMailbox, SessionStep}

import scala.concurrent.duration.{Duration, _}

private object EmailChangesScenario {
  val oldState = "oldState"
  val newState = "newState"
  val emailIds = "emailIds"
  val inbox = "inboxID"
}

class EmailChangesScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("EmailChangesScenario")
      .feed(userFeeder)
      .exec(SessionStep.retrieveAccountId)
      .during(duration.toSeconds.toInt) {
        exec(JmapMailbox.getMailboxes
          .check(statusOk, noError, JmapMailbox.saveInboxAs(inbox)))
        .exec(queryEmails(queryParameters = openpaasEmailQueryParameters(inbox))
          .check(statusOk, noError, nonEmptyListMessagesChecks(emailIds)))
        .exec(JmapEmail.getEmails()
          .check(statusOk, noError, JmapEmail.saveStateAs(oldState)))
        .exec(JmapEmail.markAsSeen()
          .check(statusOk, noError))
        .exec(JmapEmail.getNewState()
          .check(statusOk, noError, JmapEmail.saveNewStateAs(newState)))
        .pause(1 second)
      }
}
