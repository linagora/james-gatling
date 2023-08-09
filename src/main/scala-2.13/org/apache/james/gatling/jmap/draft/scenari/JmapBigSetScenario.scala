package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.JmapMailbox.numberOfSystemMailboxes
import org.apache.james.gatling.jmap.draft.RetryAuthentication._
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapMailbox, JmapMessages}

import scala.concurrent.duration.{Duration, DurationInt}

/*
 * The aim of the scenario is to provide multiple mailboxes per user and several mails in them.
 * Then, it's just checking that the created mailboxes exists.
 */
class JmapBigSetScenario {

  def generate(duration: Duration, numberOfMailboxes: Int, numberOfMessages: Int, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder = {
    def numberOfMailboxesPerUser: Int = numberOfMailboxes + numberOfSystemMailboxes

    scenario("JMAP scenario on multiple mailboxes containing multiple messages")
      .feed(userFeeder)
      .exec(CommonSteps.provisionUsersWithMailboxesAndMessages(recipientFeeder, numberOfMailboxes, numberOfMessages))
      .during(duration.toSeconds.toInt) {
        exec(execWithRetryAuthentication(JmapMailbox.getMailboxes, JmapMailbox.getMailboxesChecks(numberOfMailboxesPerUser)))
        .exec(execWithRetryAuthentication(JmapMessages.listMessages(), JmapMessages.nonEmptyListMessagesChecks))
        .pause(1 second, 2 seconds)
      }
  }

}
