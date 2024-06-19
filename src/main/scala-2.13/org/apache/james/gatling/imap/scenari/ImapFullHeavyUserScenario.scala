package org.apache.james.gatling.imap.scenari

import com.linagora.gatling.imap.PreDef._
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class ImapFullHeavyUserScenario {
  def generate(duration: Duration, feeder: FeederBuilder): ScenarioBuilder =
    scenario("ImapFullHeavyUserScenario")
      .feed(feeder)
      .exec(imap("Connect").connect()).exitHereIfFailed
      .exec(imap("login").login("#{username}", "#{password}").check(ok))
      .exec(repeat(3)(ImapCommonSteps.receiveEmail))
      .exec(repeat(2)(ImapCommonSteps.readLastEmail))
}

