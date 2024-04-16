package org.apache.james.gatling.imap.scenari

import com.linagora.gatling.imap.PreDef._
import com.linagora.gatling.imap.protocol.command.FetchAttributes.AttributeList
import com.linagora.gatling.imap.protocol.command.MessageRange.Range
import com.linagora.gatling.imap.protocol.command.MessageRanges
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.jmap.ListMessageBodyStructure
import org.apache.james.gatling.jmap.rfc8621.MailboxName

import scala.concurrent.duration._

class ImapListMessagesBodyStructureScenario {

  def generate(feeder: FeederBuilder, mailboxes: Iterable[MailboxName], mailsFetched: Int): ScenarioBuilder =
    scenario("ImapListMessagesBodyStructureScenario")
      .feed(feeder)
      .pause(1.second)
      .exec(imap("Connect").connect()).exitHereIfFailed
      .exec(imap("login").login("${username}", "${password}").check(ok))
      .exec(_.set("mailboxes", mailboxes.map(_.name)))
      .group(ListMessageBodyStructure.name) {
        exec(
          exec(imap("select")
            .select("${mailboxes.random()}")
            .check(ok))
          .exec(imap("fetch")
            .fetch(MessageRanges(Range(1, mailsFetched)), AttributeList("BODYSTRUCTURE"))
            .check(ok))
        )
      }
}
