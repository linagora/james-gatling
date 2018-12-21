package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.JmapMailboxes
import org.apache.james.gatling.jmap.scenari.common.Configuration._
import org.apache.james.gatling.jmap.scenari.common.{CommonSteps, HttpSettings}

import scala.concurrent.duration._

class JmapGetMailboxesHugeNumberScenario extends Simulation {
  val userNumber = 2
  val mailboxesNumber = 2000
  val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithSystemMailboxes(userNumber)

  val scn = scenario("JmapGetMailboxesHugeNumber")
    .exec(CommonSteps.provisionUsersWithMailboxesAndMessages(users, mailboxesNumber, 1))
    .during(ScenarioDuration) {
      JmapMailboxes.getMailboxesWithRetryAuthentication(mailboxesNumber + 3)
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(userNumber))).protocols(HttpSettings.httpProtocol)
}
