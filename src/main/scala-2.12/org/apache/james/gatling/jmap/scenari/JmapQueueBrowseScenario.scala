package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.JamesWebAdministrationQuery
import org.apache.james.gatling.control.UserFeeder.UserFeeder
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

import scala.concurrent.duration.Duration

class JmapQueueBrowseScenario {

  def generate(duration: Duration, userFeeder: UserFeeder, recipientFeeder: FeederBuilder, webadmin: JamesWebAdministrationQuery): ScenarioBuilder = {
    scenario("JmapSendMessages")
    .feed(userFeeder)
    .exec(CommonSteps.provisionSystemMailboxes())
    .during(duration) {
      exec(
        randomSwitch(
          99.0 -> JmapMessages.sendMessagesToUserWithRetryAuthentication(recipientFeeder),
          1.0 -> webadmin.getMailQueueMails("spool")))
    }
  }
}
