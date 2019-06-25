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

class ImapFullHeavyUserScenario {

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
    // The imapnio library fail to parse the attributes BODY[TEXT] and BODY[HEADER]
    // so for the time being we will only fetch the BODYSTRUCTURE
    // the corresponding issue can be found at :  https://github.com/linagora/gatling-imap/issues/38
    .exec(imap("fetch").fetch(MessageRanges(Last()), AttributeList("UID", "BODYSTRUCTURE")).check(ok))

  def generate(duration: Duration, feeder: FeederBuilder): ScenarioBuilder =
    scenario("ImapFullHeavyUserScenario")
      .feed(feeder)
      .exec(imap("Connect").connect()).exitHereIfFailed
      .exec(imap("login").login("${username}", "${password}").check(ok))
      .exec(repeat(3)(receiveEmail))
      .exec(repeat(2)(readLastEmail))
}

