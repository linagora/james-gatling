package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.User
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

import scala.concurrent.Future
import scala.concurrent.duration._

class JmapSendMessagesScenario {

  def generate(duration: Duration, users: Seq[Future[User]]): ScenarioBuilder =
    scenario("JmapSendMessages")
      .exec(CommonSteps.provisionSystemMailboxes(users))
      .during(duration) {
        exec(JmapMessages.sendMessagesRandomlyWithRetryAuthentication(users))
          .pause(1 second , 2 seconds)
      }

}
