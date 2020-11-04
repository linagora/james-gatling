package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.SelectMailbox
import org.apache.james.gatling.jmap.rfc8621.JmapEmail.{getEmails, nonEmptyEmailsChecks, nonEmptyListMessagesChecks, openpaasEmailQueryParameters, previewMessageProperties, queryEmails}
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.JmapMailbox.{getMailboxes, saveRandomMailboxWithAtLeastMessagesAs}
import org.apache.james.gatling.jmap.rfc8621.SessionStep.retrieveAccountId

class SelectMailboxScenario(minMessagesInMailbox: Int) {
  private object Keys {
    val randomMailbox = "randomMailbox"
    val emailIds = "emailIds"
  }

  def generate(userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("JmapSelectArbitraryMailboxScenario")
      .feed(userFeeder)
      .exec(retrieveAccountId)
      .group("prepare")(
        exec(getMailboxes
          .check(statusOk, noError, saveRandomMailboxWithAtLeastMessagesAs(Keys.randomMailbox, minMessagesInMailbox))))
      .group(SelectMailbox.name)(
        exec(queryEmails(queryParameters = openpaasEmailQueryParameters(Keys.randomMailbox))
          .check(statusOk, noError, nonEmptyListMessagesChecks(key = Keys.emailIds)))
        .exec(getEmails(properties = previewMessageProperties, emailIdsKey = Keys.emailIds)
          .check(statusOk, noError, nonEmptyEmailsChecks)))
}
