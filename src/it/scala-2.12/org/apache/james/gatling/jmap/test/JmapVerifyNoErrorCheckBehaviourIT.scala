package org.apache.james.gatling.jmap.test

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.check.HttpCheck
import org.apache.james.gatling.Fixture
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.JmapMessages.typicalMessageProperties
import org.apache.james.gatling.jmap.RetryAuthentication.execWithRetryAuthentication
import org.apache.james.gatling.jmap._

import scala.concurrent.duration._

class JmapVerifyNoErrorCheckBehaviourIT extends JmapIT {
  private val MAILS_NUMBER = 1

  before {
    users.foreach(server.sendMessage(Fixture.homer.username))
  }

  private def bogusGetRandomMessage(properties: List[String] = typicalMessageProperties, messageIdsKey: String = "messageIds") =
    JmapAuthentication.authenticatedQuery("getMessages (with bogus properties serialization)", "/jmap")
      .body(StringBody(
        s"""[[
          "getMessages",
          {
            "ids": ["$${$messageIdsKey.random()}"],
            "properties": [[]]
          },
          "#0"
          ]]"""))

  private def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder, randomlySentMails: Int): ScenarioBuilder = {
    import io.gatling.http.Predef._
    val noErrorCheck: Seq[HttpCheck] = List(JmapChecks.noError)
    val hasErrorCheck: Seq[HttpCheck] = List(JmapChecks.hasError)

    io.gatling.core.Predef.scenario("JmapGetMessages")
      .feed(userFeeder)
      .exec(CommonSteps.provisionUsersWithMessageList(recipientFeeder, randomlySentMails))
      .exec(execWithRetryAuthentication(JmapMessages.getRandomMessage(), noErrorCheck))
      .exec(execWithRetryAuthentication(bogusGetRandomMessage(), hasErrorCheck))
  }

  scenario((userFeederBuilder, recipientFeederBuilder) => {
    generate(10 seconds, userFeederBuilder, recipientFeederBuilder, MAILS_NUMBER)
  })

}
