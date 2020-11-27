package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeeder
import org.apache.james.gatling.control.{Password, User, UserFeeder, Username}
import org.apache.james.gatling.imap.scenari.PlatformValidationScenario
import org.apache.james.gatling.simulation.Configuration.{InjectionDuration, ScenarioDuration, UserCount}
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

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

  val feeder: UserFeeder = UserFeeder.toFeeder(authenticatedUsers)

  setUp(new PlatformValidationScenario()
      .generate(duration = ScenarioDuration, userFeeder = feeder)
    .inject(rampUsers(UserCount) during InjectionDuration)
    .protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build()))
}
