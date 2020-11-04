package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.OpenMessage
import org.apache.james.gatling.jmap.rfc8621.JmapEmail.{getRandomEmails, nonEmptyListMessagesChecks, openpaasEmailQueryParameters, queryEmails, typicalMessageProperties}
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.JmapMailbox.{getMailboxes, saveInboxAs}
import org.apache.james.gatling.jmap.rfc8621.SessionStep.retrieveAccountId
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapHttp}

class OpenEmailScenario {
  private object Keys {
    val inbox = "inboxID"
    val emailIds = "emailIds"
  }

  def generate(userFeeder: UserFeederBuilder): ScenarioBuilder = scenario("JmapOpenArbitraryMessageScenario")
    .feed(userFeeder)
    .exec(retrieveAccountId)
    .group("prepare")(
      exec(getMailboxes
        .check(statusOk, noError, saveInboxAs(Keys.inbox)))
      .exec(queryEmails(queryParameters = openpaasEmailQueryParameters(Keys.inbox))
        .check(statusOk, noError, nonEmptyListMessagesChecks(key = Keys.emailIds))))
    .group(OpenMessage.name)(
      exec(getRandomEmails(typicalMessageProperties, Keys.emailIds)
        .check(JmapHttp.statusOk, JmapHttp.noError, JmapEmail.nonEmptyEmailsChecks)))
}
