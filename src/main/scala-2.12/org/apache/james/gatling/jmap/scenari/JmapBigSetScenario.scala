package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.User
import org.apache.james.gatling.jmap.JmapMailboxes.numberOfSystemMailboxes
import org.apache.james.gatling.jmap.RetryAuthentication._
import org.apache.james.gatling.jmap.{CommonSteps, JmapMailboxes, JmapMessages}

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, DurationInt}

/*
 * The aim of the scenario is to provide multiple mailboxes per user and several mails in them.
 * Then, it's just checking that the created mailboxes exists.
 */
class JmapBigSetScenario extends Simulation {

  def generate(duration: Duration, numberOfMailboxes: Int, numberOfMessages: Int, users: Seq[Future[User]]): ScenarioBuilder = {
    def numberOfMailboxesPerUser: Int = numberOfMailboxes + numberOfSystemMailboxes

    scenario("JMAP scenario on multiple mailboxes containing multiple messages")
      .exec(CommonSteps.provisionUsersWithMailboxesAndMessages(users, numberOfMailboxes, numberOfMessages))
      .during(duration) {
        execWithRetryAuthentication(JmapMailboxes.getMailboxes, JmapMailboxes.getMailboxesChecks(numberOfMailboxesPerUser))
          .exec(execWithRetryAuthentication(JmapMessages.listMessages, JmapMessages.listMessagesChecks))
          .pause(1 second, 2 seconds)
      }
  }

}
