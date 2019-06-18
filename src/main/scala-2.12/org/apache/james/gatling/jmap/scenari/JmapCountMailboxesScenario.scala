package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.RetryAuthentication._
import org.apache.james.gatling.jmap.{CommonSteps, JmapMailbox}

import scala.concurrent.duration.{Duration, DurationInt}

/*
 * The aim of the scenario is to count the number of mailboxes.
 * No mailboxes are created manually, only the system mailboxes whose are automatically provisioned should be present.
 */
class JmapCountMailboxesScenario {


  def generate(duration: Duration, userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("JMAP scenario counting system mailboxes")
      .feed(userFeeder)
      .exec(CommonSteps.authentication())
      .exec(execWithRetryAuthentication(JmapMailbox.getMailboxes, JmapMailbox.storeMailboxIds))
      .during(duration) {
        execWithRetryAuthentication(JmapMailbox.getMailboxes, JmapMailbox.checkSystemMailboxIdsHaveNotChanged)
          .pause(1 second, 2 seconds)
      }

}
