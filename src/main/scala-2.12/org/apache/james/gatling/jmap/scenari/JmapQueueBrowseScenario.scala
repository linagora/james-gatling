package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.{JamesWebAdministrationQuery, User}
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

import scala.concurrent.Future
import scala.concurrent.duration.Duration

class JmapQueueBrowseScenario extends Simulation {

  def generate(duration: Duration, users: Seq[Future[User]], webadmin: JamesWebAdministrationQuery): ScenarioBuilder = {
    scenario("JmapSendMessages")
    .exec(CommonSteps.provisionSystemMailboxes(users))
    .during(duration) {
      exec(
        randomSwitch(
          99.0 -> JmapMessages.sendMessagesRandomlyWithRetryAuthentication(users),
          1.0 -> webadmin.getMailQueueMails("spool")))
    }
  }
}
