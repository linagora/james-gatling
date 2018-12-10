package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef.{Simulation, atOnceUsers, scenario}
import org.apache.james.gatling.configuration.Configuration.{BaseJamesWebAdministrationUrl, ScenarioDuration, UserCount}
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.JmapMailboxes
import org.apache.james.gatling.jmap.scenari.common.{CommonSteps, HttpSettings}
import org.apache.james.gatling.utils.RetryAuthentication.execWithRetryAuthentication

import scala.concurrent.duration.DurationInt

/*
 * The aim of the scenario is to count the number of mailboxes.
 * No mailboxes are created manually, only the system mailboxes whose are automatically provisioned should be present.
 */
class JmapCountMailboxesScenario extends Simulation {

  val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsers(UserCount)

  val scn = scenario("JMAP scenario counting system mailboxes")
    .exec(CommonSteps.authentication(users))
    .exec(execWithRetryAuthentication(JmapMailboxes.getMailboxes, JmapMailboxes.storeMailboxIds))
    .during(ScenarioDuration) {
      execWithRetryAuthentication(JmapMailboxes.getMailboxes, JmapMailboxes.checkSystemMailboxIdsHaveNotChanged)
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)
}
