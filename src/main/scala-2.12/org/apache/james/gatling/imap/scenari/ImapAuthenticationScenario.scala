package org.apache.james.gatling.imap.scenari

import java.util.Calendar

import com.linagora.gatling.imap.PreDef._
import com.linagora.gatling.imap.protocol.Uid
import com.linagora.gatling.imap.protocol.command.FetchAttributes.AttributeList
import com.linagora.gatling.imap.protocol.command.MessageRange.{From, One, Range, To}
import com.linagora.gatling.imap.protocol.command.MessageRanges
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

class ImapAuthenticationScenario {

  def generate(duration: Duration, feeder: FeederBuilder): ScenarioBuilder =
    scenario("ImapAuthentication")
      .feed(feeder)
      .pause(1.second)
      .during(duration) {
        exec(imap("Connect").connect()).exitHereIfFailed
        .exec(imap("login").login("${username}", "${password}").check(ok))
        .exec(imap("list").list("", "*").check(ok, hasFolder("INBOX")))
        .exec(imap("select").select("INBOX").check(ok, hasRecent(0)))
        .exec(imap("append").append("INBOX", Some(scala.collection.immutable.Seq("\\Flagged")), Option.empty[Calendar],
            """From: expeditor@example.com
              |To: recipient@example.com
              |Subject: test subject
              |
              |Test content""".stripMargin).check(ok))
        .exec(imap("fetch").fetch(MessageRanges(One(1), One(2), Range(3, 5), From(3), One(8), To(1)), AttributeList("BODY", "UID")).check(ok, hasUid(Uid(1)), contains("TEXT")))
      }
}
