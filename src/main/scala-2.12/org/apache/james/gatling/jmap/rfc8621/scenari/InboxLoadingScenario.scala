package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.InboxHomeLoading
import org.apache.james.gatling.jmap.rfc8621.JmapEmail.{getEmails, nonEmptyEmailsChecks, nonEmptyListMessagesChecks, openpaasEmailQueryParameters, previewMessageProperties, queryEmails}
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.JmapMailbox.{getMailboxes, saveInboxAs}
import org.apache.james.gatling.jmap.rfc8621.SessionStep.retrieveAccountId

class InboxLoadingScenario {
  private object Keys {
    val inbox = "inboxID"
    val emailIds = "emailIds"
  }

  def generate(userFeeder: UserFeederBuilder): ScenarioBuilder = scenario("JmapHomeLoadingScenario")
    .feed(userFeeder)
    .exec(retrieveAccountId)
    .group(InboxHomeLoading.name)(
      exec(getMailboxes
        .check(statusOk, noError, saveInboxAs(Keys.inbox)))
      .exec(queryEmails(queryParameters = openpaasEmailQueryParameters(Keys.inbox))
        .check(statusOk, noError, nonEmptyListMessagesChecks(key = Keys.emailIds)))
      .exec(getEmails(properties = previewMessageProperties, emailIdsKey = Keys.emailIds)
        .check(statusOk, noError, nonEmptyEmailsChecks)))
}
