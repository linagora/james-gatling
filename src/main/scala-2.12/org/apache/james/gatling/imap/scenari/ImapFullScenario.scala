package org.apache.james.gatling.imap.scenari

import java.util.Calendar

import com.linagora.gatling.imap.PreDef._
import com.linagora.gatling.imap.protocol.command.FetchAttributes.AttributeList
import com.linagora.gatling.imap.protocol.command.MessageRange.Last
import com.linagora.gatling.imap.protocol.command.MessageRanges
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class ImapFullScenario {

  val receiveEmail = exec(imap("append").append("INBOX", Some(scala.collection.immutable.Seq("\\Flagged")), Option.empty[Calendar],
    """From: expeditor@example.com
      |To: recipient@example.com
      |Subject: test subject
      |
      |Test content
      |abcdefghijklmnopqrstuvwxyz
      |0123456789""".stripMargin).check(ok))

  val readLastEmail = exec(imap("list").list("", "*").check(ok, hasFolder("INBOX")))
    .exec(imap("select").select("INBOX").check(ok))
    .exec(imap("fetch").fetch(MessageRanges(Last()), AttributeList("BODY[HEADER]", "UID", "BODY[TEXT]")).check(ok))

  val heavyUser = repeat(3)(receiveEmail)
    .repeat(2)(readLastEmail)
  val lightUser = receiveEmail
    .exec(readLastEmail)

  def generate(duration: Duration, feeder: FeederBuilder): ScenarioBuilder =
    scenario("Imap")
      .feed(feeder)
      .pause(1.second)
      .exec(imap("Connect").connect()).exitHereIfFailed
      .exec(imap("login").login("${username}", "${password}").check(ok))
      .during(duration) {
        randomSwitch(
          75.0 -> exec(lightUser),
          25.0 -> exec(heavyUser)
        )
      }
}