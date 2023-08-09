package org.apache.james.gatling.jmap.draft.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.JamesWebAdministrationQuery
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapMessages}

import scala.concurrent.duration.Duration

class JmapQueueBrowseScenario {

  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder, webadmin: JamesWebAdministrationQuery): ScenarioBuilder = {
    scenario("JmapSendMessages")
    .feed(userFeeder)
    .exec(CommonSteps.provisionSystemMailboxes())
    .during(duration.toSeconds.toInt) {
      exec(
        randomSwitch(
          99.0 -> JmapMessages.sendMessagesToUserWithRetryAuthentication(recipientFeeder),
          1.0 -> webadmin.getMailQueueMails("spool")))
    }
  }
}
