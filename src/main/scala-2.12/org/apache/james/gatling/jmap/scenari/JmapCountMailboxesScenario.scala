package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.User
import org.apache.james.gatling.jmap.RetryAuthentication._
import org.apache.james.gatling.jmap.{CommonSteps, JmapMailboxes}

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, DurationInt}
/*
 * The aim of the scenario is to count the number of mailboxes.
 * No mailboxes are created manually, only the system mailboxes whose are automatically provisioned should be present.
 */
class JmapCountMailboxesScenario {


  def generate(duration: Duration, users: Seq[Future[User]]): ScenarioBuilder =
    scenario("JMAP scenario counting system mailboxes")
      .exec(CommonSteps.authentication(users))
      .exec(execWithRetryAuthentication(JmapMailboxes.getMailboxes, JmapMailboxes.storeMailboxIds))
      .during(duration) {
        execWithRetryAuthentication(JmapMailboxes.getMailboxes, JmapMailboxes.checkSystemMailboxIdsHaveNotChanged)
          .pause(1 second , 2 seconds)
      }

}
