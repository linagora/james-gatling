package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.jmap.JmapMessages._
import org.apache.james.gatling.jmap.JmapMailbox._
import org.apache.james.gatling.jmap._

class JmapOpenArbitraryMessageScenario {

  private object Keys {
    val inbox = "inboxID"
    val messageIds = "messageIds"
    val messagesDetailList = "messagesDetailList"
  }

  private val isSuccess: Seq[HttpCheck] = List(
    status.is(200),
    JmapChecks.noError)


  private val openArbitraryMessage: HttpRequestBuilder = getRandomMessage(openpaasInboxOpenMessageProperties, Keys.messageIds)

  def generate(feederBuilder: FeederBuilder): ScenarioBuilder = {
    scenario("JmapOpenArbitraryMessageScenario")
      .feed(feederBuilder)
      .exec(CommonSteps.authentication())
      .group("prepare")(
        exec(RetryAuthentication.execWithRetryAuthentication(getMailboxes, isSuccess ++ JmapMailbox.saveInboxAs(Keys.inbox)))
          .exec(RetryAuthentication.execWithRetryAuthentication(JmapMessages.listMessages(openpaasListMessageParameters(Keys.inbox)), JmapMessages.listMessagesChecks))
      )
      .group(OpenMessage.name)(
        exec(RetryAuthentication.execWithRetryAuthentication(openArbitraryMessage, isSuccess)))
  }
}
