package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.JmapMessages
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.jmap.scenari.common.{CommonSteps, HttpSettings}
import org.apache.james.gatling.utils.RetryAuthentication._

import scala.concurrent.duration._

class JmapGetMessageListScenario extends Simulation {

  val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)

  val scn = scenario("JmapGetMessagesLists")
    .exec(CommonSteps.provisionUsersWithMessages(users))
    .during(ScenarioDuration) {
      execWithRetryAuthentication(JmapMessages.listMessages(), JmapMessages.listMessagesChecks)
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)

}
