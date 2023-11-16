package org.apache.james.gatling.imap.scenari

import com.linagora.gatling.imap.PreDef.{hasFolder, imap, ok}
import com.linagora.gatling.imap.protocol.command.FetchAttributes.AttributeList
import com.linagora.gatling.imap.protocol.command.MessageRange.{Last, Range}
import com.linagora.gatling.imap.protocol.command.{MessageRanges, Silent, StoreFlags}
import com.linagora.gatling.imap.protocol.{Messages, Recent, StatusItems, UidNext, Unseen}
import io.gatling.core.Predef._
import io.gatling.core.structure._
import javax.mail.Flags
import javax.mail.search.FlagTerm
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder

import scala.concurrent.duration._

class PlatformValidationScenario(minWaitDelay: FiniteDuration = 2 seconds, maxWaitDelay: FiniteDuration = 18 seconds)  {

  val initialConnection: ChainBuilder = group("initialConnection")(
    exec(imap("Connect").connect()).exitHereIfFailed
      .exec(imap("capability").capability().check(ok))
      .exec(imap("login").login("${username}", "${password}").check(ok)).exitHereIfFailed
      .exec(imap("capability").capability().check(ok))
      .exec(imap("enableUTF8").enable("UTF8=ACCEPT").check(ok))
      .exec(imap("namespace").namespace().check(ok))
      .exec(imap("list").list("", "*").check(ok))
      .exec(imap("lsub").lsub("", "*").check(ok))
      .exec(imap("myrights").myRights("INBOX").check(ok))
      .exec(imap("select").select("INBOX").check(ok)).exitHereIfFailed
      .exec(imap("flagResync").fetch(MessageRanges(Range(1, 1000000)), AttributeList("UID", "FLAGS"))))

  val closeConnection: ChainBuilder = group("closeConnection")(
    exec(imap("close").close().check(ok))
      .exec(imap("logout").logout().check(ok)))

  val coreActions: ChainBuilder = group("coreActions")(
    randomSwitch(
      30.0 -> exec(imap("status").status("INBOX", StatusItems(Seq(UidNext, Messages, Unseen, Recent))).check(ok)),
      15.0 -> exec(imap("idle").idle().check(ok)),
      9.0 -> exec(imap("getQuotaRoot").getQuotaRoot("INBOX").check(ok)),
      8.0 -> exec(ImapCommonSteps.receiveEmail), // Higher probability that what is seen in the wild to replace SMTP for message delivery
      8.0 -> exec(imap("noop").noop().check(ok)),
      9.0 -> exec(imap("fetchHeaders").uidFetch(MessageRanges(Last()), AttributeList("UID", "RFC822.SIZE", "FLAGS", "BODY.PEEK[HEADER.FIELDS (From To Cc Bcc Subject Date Message-ID Priority X-Priority References Newsgroups In-Reply-To Content-Type Reply-To)]")).check(ok)),
      9.0 -> exec(imap("fetchBody").uidFetch(MessageRanges(Last()), AttributeList("UID", "RFC822.SIZE", "BODY.PEEK[]")).check(ok)),
      2.0 -> exec(imap("searchUnseen").uidSearch(MessageRanges(Range(1, 100000)), new FlagTerm(new Flags(Flags.Flag.SEEN), false)).check(ok)),
      2.0 -> exec(imap("searchDeleted").uidSearch(MessageRanges(Range(1, 100000)), new FlagTerm(new Flags(Flags.Flag.DELETED), false)).check(ok)),
      2.0 -> exec(imap("select").select("INBOX").check(ok)),
      2.0 -> exec(imap("store").store(MessageRanges(Last()), StoreFlags.add(Silent.Enable(), "\\Seen")).check(ok)),
      1.0 -> exec(imap("list").list("", "*").check(ok, hasFolder("INBOX"))),
      1.0 -> exec(imap("unselect").unselect().check(ok))
        .exec(imap("select").select("INBOX").check(ok)).exitHereIfFailed,
      1.0 -> exec(imap("check").check().check(ok)),
      1.0 -> exec(imap("expunge").expunge().check(ok))))

  def generate(duration: Duration, userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("ImapPlatformValidation")
      .feed(userFeeder)
      .exec(initialConnection)
      .during(duration.toSeconds.toInt) {
        coreActions.pause(minWaitDelay, maxWaitDelay)
      }
      .exec(closeConnection)
}
