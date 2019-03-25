package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.JmapMailboxes.numberOfSystemMailboxes
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.jmap.scenari.common.{CommonSteps, HttpSettings}
import org.apache.james.gatling.jmap.{JmapMailboxes, JmapMessages}
import org.apache.james.gatling.utils.RetryAuthentication.execWithRetryAuthentication

import scala.concurrent.duration.DurationInt

/*
 * The aim of the scenario is to provide multiple mailboxes per user and several mails in them.
 * Then, it's just checking that the created mailboxes exists.
 */
class JmapBigSetScenario extends Simulation {

  private val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)
  private val scn = scenario("JMAP scenario on multiple mailboxes containing multiple messages")
    .exec(CommonSteps.provisionUsersWithMailboxesAndMessages(users, NumberOfMailboxes, NumberOfMessages))
    .during(ScenarioDuration) {
      execWithRetryAuthentication(JmapMailboxes.getMailboxes, JmapMailboxes.getMailboxesChecks(numberOfMailboxesPerUser))
        .exec(execWithRetryAuthentication(JmapMessages.listMessages, JmapMessages.listMessagesChecks))
        .pause(1 second, 2 seconds)
    }

  def numberOfMailboxesPerUser: Int =
    NumberOfMailboxes + numberOfSystemMailboxes

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)
}
