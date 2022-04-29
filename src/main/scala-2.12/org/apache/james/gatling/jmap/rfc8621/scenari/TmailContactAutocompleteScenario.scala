package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.control.RecipientFeeder.{RecipientFeederBuilder, recipientSessionParam}
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.rfc8621.JmapHttp.{noError, statusOk}
import org.apache.james.gatling.jmap.rfc8621.scenari.TmailContactAutocompleteScenario.typeaheadKeyword
import org.apache.james.gatling.jmap.rfc8621.{JmapMailbox, JmapTmailContact, SessionStep}

import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.Random

object TmailContactAutocompleteScenario {
  val typeaheadKeyword: String = "typeaheadKeyword"
}

class TmailContactAutocompleteScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("TmailContactAutocompleteScenario")
      .feed(userFeeder)
      .exec(SessionStep.retrieveAccountId)
      .exec(JmapMailbox.provisionUsersWithMessages(recipientFeeder, numberOfMessages = 5))
      .pause(2 seconds)
      .during(duration) {
        randomSwitch(
          70.0 -> exec((session: Session) => session.set(typeaheadKeyword, randomSubString(session.attributes(recipientSessionParam).asInstanceOf[String]))),
          30.0 -> exec((session: Session) => session.set(typeaheadKeyword, Random.alphanumeric.take(5).mkString(""))))
          .exec(JmapTmailContact.getAutocomplete(typeaheadKeyword = typeaheadKeyword).check(statusOk, noError))
          .pause(5 seconds)
      }

  private def randomSubString(input: String): String = {
    val length: Int = Random.nextInt((input.length - 1) + 1)
    val randomNum: Int = Random.nextInt(input.length - length + 1)
    input.substring(randomNum, randomNum + length)
  }
}

