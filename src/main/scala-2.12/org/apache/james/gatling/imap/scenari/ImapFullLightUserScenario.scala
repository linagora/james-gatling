package org.apache.james.gatling.imap.scenari

import com.linagora.gatling.imap.PreDef._
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class ImapFullLightUserScenario {
  def generate(duration: Duration, feeder: FeederBuilder): ScenarioBuilder =
    scenario("ImapFullLightUserScenario")
      .feed(feeder)
      .exec(imap("Connect").connect()).exitHereIfFailed
      .exec(imap("login").login("${username}", "${password}").check(ok))
      .exec(ImapCommonSteps.receiveEmail)
      .exec(ImapCommonSteps.readLastEmail)
}
