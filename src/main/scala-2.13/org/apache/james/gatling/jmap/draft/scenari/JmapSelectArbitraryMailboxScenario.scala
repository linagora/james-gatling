package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.SelectMailbox
import org.apache.james.gatling.jmap.draft.JmapMailbox._
import org.apache.james.gatling.jmap.draft.JmapMessages._
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapChecks, JmapMailbox, JmapMessages, RetryAuthentication}

class JmapSelectArbitraryMailboxScenario(minMessagesInMailbox: Int) {

  private object Keys {
    val randomMailbox = "randomMailbox"
    val messageIds = "messageIds"
    val messagesDetailList = "messagesDetailList"
  }

  private val isSuccess: Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError)


  private val openArbitraryMailboxes: HttpRequestBuilder = getRandomMessages(openpaasInboxOpenMessageProperties, Keys.messageIds)

  def generate(userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("JmapSelectArbitraryMailboxScenario")
      .feed(userFeeder)
      .exec(CommonSteps.authentication())
      .group("prepare")(prepare)
      .group(SelectMailbox.name)(selectArbitrary)

  def selectArbitrary: ChainBuilder =
    exec(RetryAuthentication.execWithRetryAuthentication(
      JmapMessages.listMessages(openpaasListMessageParameters(Keys.randomMailbox)),
      JmapMessages.nonEmptyListMessagesChecks))
      .exec(RetryAuthentication.execWithRetryAuthentication(JmapMessages.getMessages(JmapMessages.previewMessageProperties, Keys.messageIds), isSuccess))

  def prepare: ChainBuilder =
    exec(RetryAuthentication.execWithRetryAuthentication(getMailboxes, isSuccess ++ JmapMailbox.saveRandomMailboxWithAtLeastMessagesAs(Keys.randomMailbox, minMessagesInMailbox)))
}
