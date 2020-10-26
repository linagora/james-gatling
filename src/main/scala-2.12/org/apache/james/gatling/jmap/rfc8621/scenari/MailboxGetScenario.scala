package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef.scenario
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder

import scala.concurrent.duration.Duration

class MailboxGetScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("MailboxGetScenario")
      .feed(userFeeder)
//      .exec(CommonSteps.authentication())
//      .during(duration) {
//        JmapMailbox.getSystemMailboxesWithRetryAuthentication
//          .pause(1 second , 2 seconds)
//      }
}
