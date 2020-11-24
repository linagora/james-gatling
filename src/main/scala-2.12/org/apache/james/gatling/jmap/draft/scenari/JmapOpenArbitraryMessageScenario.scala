package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.OpenMessage
import org.apache.james.gatling.jmap.draft.JmapMailbox._
import org.apache.james.gatling.jmap.draft.JmapMessages._
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapChecks, JmapMailbox, JmapMessages, RetryAuthentication}

class JmapOpenArbitraryMessageScenario {

  private object Keys {
    val inbox = "inboxID"
    val messageIds = "messageIds"
    val messagesDetailList = "messagesDetailList"
  }

  private val isSuccess: Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError)


  private val openArbitraryMessage: HttpRequestBuilder = getRandomMessages(openpaasInboxOpenMessageProperties, Keys.messageIds)

  def generate(userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("JmapOpenArbitraryMessageScenario")
      .feed(userFeeder)
      .exec(CommonSteps.authentication())
      .group("prepare")(prepare)
      .group(OpenMessage.name)(openArbitrary)

  def openArbitrary: ChainBuilder =
    exec(RetryAuthentication.execWithRetryAuthentication(openArbitraryMessage, isSuccess))

  def prepare: ChainBuilder =
    exec(RetryAuthentication.execWithRetryAuthentication(getMailboxes, isSuccess ++ JmapMailbox.saveInboxAs(Keys.inbox)))
      .exec(RetryAuthentication.execWithRetryAuthentication(JmapMessages.listMessages(openpaasListMessageParameters(Keys.inbox)), JmapMessages.nonEmptyListMessagesChecks))
}
