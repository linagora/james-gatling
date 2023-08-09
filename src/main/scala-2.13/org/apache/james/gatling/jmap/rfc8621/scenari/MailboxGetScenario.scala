package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.rfc8621.{JmapHttp, JmapMailbox, SessionStep}

import scala.concurrent.duration._

class MailboxGetScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("MailboxGetScenario")
      .feed(userFeeder)
      .exec(SessionStep.retrieveAccountId)
      .during(duration.toSeconds.toInt) {
        exec(JmapMailbox.getMailboxes
            .check(JmapHttp.statusOk, JmapHttp.noError))
          .pause(1 second)
      }
}
