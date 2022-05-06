package org.apache.james.gatling.imap.scenari

import com.linagora.gatling.imap.PreDef._
import com.linagora.gatling.imap.protocol.command.MessageRange.{From, One}
import com.linagora.gatling.imap.protocol.command.{MessageRanges, Silent, StoreFlags}
import io.gatling.commons.validation.{SuccessWrapper, Validation}
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.session.Expression
import io.gatling.core.session.el._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.imap.scenari.ImapStoreScenario.validationSequence

import java.util.Calendar
import scala.concurrent.duration._
import scala.language.implicitConversions
import scala.util.Random

object ImapStoreScenario {
  def validationSequence[T](seq: Seq[Validation[T]]): Validation[Seq[T]] =
    seq.foldLeft(Seq.empty[T].success) { (acc, validation) =>
      for (accValue <- acc; value <- validation) yield accValue :+ value
    }
}

class ImapStoreScenario {
  private val numberOfMailInInbox = 5000
  private val appendGracePeriod = 5 milliseconds

  private val populateMailbox = exec(imap("append").append("INBOX", Option.empty[scala.collection.immutable.Seq[String]], Option.empty[Calendar],
    """From: expeditor@example.com
      |To: recipient@example.com
      |Subject: test subject
      |
      |Test content
      |abcdefghijklmnopqrstuvwxyz
      |0123456789""".stripMargin).check(ok))

  private val populateInbox = repeat(numberOfMailInInbox)(pause(appendGracePeriod).exec(populateMailbox))

  private implicit def storeFlags2Expression(value: StoreFlags): Expression[StoreFlags] = session =>
    validationSequence(value.flags.map(flag => flag.el[String]).map(expr => expr(session)))
      .map(mutable => collection.immutable.Seq(mutable:_*))
      .map(flags => value.setFlags(flags))

  private val rangeFlagsUpdates = imap("storeAll").store(MessageRanges(From(1L)), StoreFlags.add(Silent.Enable(), "all${loopId}")).check(ok)
  private val singleFlagsUpdate = imap("storeOne").store(session => MessageRanges(One(1L + Random.nextInt(numberOfMailInInbox))), StoreFlags.add(Silent.Enable(), "one${loopId}")).check(ok)

  def generate(duration: Duration, feeder: FeederBuilder): ScenarioBuilder =
    scenario("imap store scenario")
      .feed(feeder)
      .pause(1.second)
      .during(duration) {
        exec(imap("Connect").connect()).exitHereIfFailed
        .exec(imap("login").login("${username}", "${password}").check(ok))
        .exec(imap("select").select("INBOX").check(ok))
        .exec(populateInbox)
        .pause(1 second)
        .repeat(10, "loopId") (exec(rangeFlagsUpdates).pause(1 second).exec(singleFlagsUpdate))
      }
}
