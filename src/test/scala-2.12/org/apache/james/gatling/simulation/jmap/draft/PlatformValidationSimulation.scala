package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import org.apache.james.gatling.control.AuthenticatedUserFeeder.AuthenticatedUserFeederBuilder
import org.apache.james.gatling.control.{Password, RecipientFeeder, User, UserFeeder, Username}
import org.apache.james.gatling.jmap.draft.scenari.PlatformValidationScenario
import org.apache.james.gatling.simulation.Configuration.{InjectionDuration, ScenarioDuration, UserCount}
import org.apache.james.gatling.simulation.HttpSettings

class PlatformValidationSimulation extends Simulation {
  private def recordValueToString(recordValue: Any): String = recordValue match {
    case s: String => s
    case a: Any => println("Warning: calling toString on a feeder value"); a.toString
  }

  val authenticatedUsers: Seq[User] = csv("users.csv").readRecords
    .map(record =>
      User(
        username = Username(recordValueToString(record("username"))),
        password = Password(recordValueToString(record("password")))))

  val feeder: AuthenticatedUserFeederBuilder = UserFeeder.toFeeder(authenticatedUsers)

  setUp(new PlatformValidationScenario(minMessagesInMailbox = 10)
      .generate(duration = ScenarioDuration, userFeeder = feeder, recipientFeeder = RecipientFeeder.usersToFeeder(authenticatedUsers))
    .inject(rampUsers(UserCount) during InjectionDuration)
    .protocols(HttpSettings.httpProtocol))
}
