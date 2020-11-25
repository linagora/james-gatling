package org.apache.james.gatling.imap.scenari

import com.linagora.gatling.imap.PreDef.{imap, ok}
import io.gatling.core.Predef._
import io.gatling.core.structure.{ScenarioBuilder, _}
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder

import scala.concurrent.duration.{Duration, _}

class PlatformValidationScenario(minWaitDelay: Duration = 1 minute, maxWaitDelay: Duration = 5 minutes)  {

  val lightScenario: ChainBuilder = exec(ImapCommonSteps.receiveEmail)
    .exec(ImapCommonSteps.readLastEmail)

  val heavyScenario: ChainBuilder = exec(repeat(3)(ImapCommonSteps.receiveEmail))
    .exec(repeat(2)(ImapCommonSteps.readLastEmail))

  def generate(duration: Duration, userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("ImapPlatformValidation")
      .feed(userFeeder)
      .exec(imap("Connect").connect()).exitHereIfFailed
      .exec(imap("login").login("${username}", "${password}").check(ok))
      .during(duration) {
        randomSwitch(
          25.0 -> group("lightScenario")(lightScenario),
          75.0 -> group("heavyScenario")(heavyScenario))
          .pause(minWaitDelay, maxWaitDelay)
      }
}
