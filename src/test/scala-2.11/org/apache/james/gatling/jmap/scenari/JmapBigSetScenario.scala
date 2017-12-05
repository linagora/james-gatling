package org.apache.james.gatling.jmap.scenari

import scala.concurrent.duration.DurationInt

import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.JmapMessages
import org.apache.james.gatling.jmap.scenari.common.CommonSteps
import org.apache.james.gatling.jmap.scenari.common.Configuration.BaseJamesWebAdministrationUrl
import org.apache.james.gatling.jmap.scenari.common.Configuration.ScenarioDuration
import org.apache.james.gatling.jmap.scenari.common.Configuration.UserCount
import org.apache.james.gatling.jmap.scenari.common.HttpSettings
import org.apache.james.gatling.utils.RetryAuthentication.execWithRetryAuthentication

import io.gatling.core.Predef.Simulation
import io.gatling.core.Predef.atOnceUsers
import io.gatling.core.Predef.scenario

/*
 * The aim of the scenario is to provide multiple mailboxes per user and several mails in them.
 * Then, it's just checking that the created mailboxes exists.
 */
class JmapBigSetScenario extends Simulation {

  val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)
  
  val scn = scenario("JmapBigSet")
    .exec(CommonSteps.provisionUsersWithMailboxesAndMessages(users))
    .during(ScenarioDuration) {
      execWithRetryAuthentication(JmapMessages.listMessages(), JmapMessages.listMessagesChecks)
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)
}
