package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.jmap.CommonSteps.UserPicker
import org.apache.james.gatling.jmap.JmapMailbox.numberOfSystemMailboxes
import org.apache.james.gatling.jmap.RetryAuthentication._
import org.apache.james.gatling.jmap.{CommonSteps, JmapMailbox, JmapMessages}

import scala.concurrent.duration.{Duration, DurationInt}

/*
 * The aim of the scenario is to provide multiple mailboxes per user and several mails in them.
 * Then, it's just checking that the created mailboxes exists.
 */
class JmapBigSetScenario {

  def generate(duration: Duration, numberOfMailboxes: Int, numberOfMessages: Int, userPicker: UserPicker): ScenarioBuilder = {
    def numberOfMailboxesPerUser: Int = numberOfMailboxes + numberOfSystemMailboxes

    scenario("JMAP scenario on multiple mailboxes containing multiple messages")
      .exec(CommonSteps.provisionUsersWithMailboxesAndMessages(userPicker, numberOfMailboxes, numberOfMessages))
      .during(duration) {
        execWithRetryAuthentication(JmapMailbox.getMailboxes, JmapMailbox.getMailboxesChecks(numberOfMailboxesPerUser))
          .exec(execWithRetryAuthentication(JmapMessages.listMessages, JmapMessages.listMessagesChecks))
          .pause(1 second, 2 seconds)
      }
  }

}
