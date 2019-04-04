package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.User
import org.apache.james.gatling.jmap.{CommonSteps, JmapMessages}

import scala.concurrent.Future
import scala.concurrent.duration._

class JmapMessageUpdateScenario extends Simulation {

  def generate(duration: Duration, users: Seq[Future[User]], randomlySentMails: Int): ScenarioBuilder =
    scenario("JmapUpdateMessages")
      .exec(CommonSteps.provisionUsersWithMessageList(users, randomlySentMails))
      .during(duration) {
        randomSwitch(
          70.0 -> exec(JmapMessages.markAsRead()),
          20.0 -> exec(JmapMessages.markAsAnswered()),
          10.0 -> exec(JmapMessages.markAsFlagged())
        )
          .pause(1 second , 2 seconds)
      }
}
