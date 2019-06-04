package org.apache.james.gatling.imap.scenari

import com.linagora.gatling.imap.PreDef._
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class ImapAuthenticationScenario {

  def generate(duration: Duration, feeder: FeederBuilder): ScenarioBuilder =
    scenario("ImapAuthentication")
      .feed(feeder)
      .pause(1.second)
      .exec(imap("Connect").connect()).exitHereIfFailed
      .exec(imap("login").login("${username}", "${password}").check(ok))
      .exec(imap("list").list("", "*").check(ok, hasFolder("INBOX")))
      .exec(imap("select").select("INBOX").check(ok, hasRecent(0)))
}
