package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.MessageFlagUpdates
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.JmapMailbox.{getMailboxes, saveInboxAs}
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapHttp, SessionStep}

class EmailKeywordsUpdatesScenario {
  private object Keys {
    val inbox = "inboxID"
  }

  def generate(userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("EmailKeywordsUpdatesScenario")
      .feed(userFeeder)
      .exec(SessionStep.retrieveAccountId)
      .group(MessageFlagUpdates.name)(
        exec(getMailboxes
          .check(statusOk, noError, saveInboxAs(Keys.inbox)))
        .exec(JmapEmail.queryEmails()
          .check(JmapHttp.statusOk, JmapHttp.noError, JmapEmail.nonEmptyListMessagesChecks()))
        .randomSwitch(
          70.0 -> exec(JmapEmail.markAsSeen()),
          20.0 -> exec(JmapEmail.markAsAnswered()),
          10.0 -> exec(JmapEmail.markAsFlagged())))
}
