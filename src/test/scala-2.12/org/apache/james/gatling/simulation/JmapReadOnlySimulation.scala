package org.apache.james.gatling.simulation

import org.apache.james.gatling.control._
import org.apache.james.gatling.control.AuthenticatedUserFeeder._
import org.apache.james.gatling.jmap.scenari.JmapReadOnlyScenario

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

class JmapReadOnlySimulation extends Simulation {

  private def recordValueToString(recordValue: Any): String = recordValue match {
    case s: String => s
    case a: Any => println("Warning: calling toString on a feeder value"); a.toString
  }

  val authenticatedUsers: Iterator[AuthenticatedUser] = csv("usersAuthenticated.csv").readRecords
    .map(record =>
      AuthenticatedUser(
        username = Username(recordValueToString(record("username"))),
        accessToken = AccessToken(recordValueToString(record("accessToken")))))
    .toIterator

  val feeder: AuthenticatedUserFeederBuilder = AuthenticatedUserFeeder.toFeeder(authenticatedUsers)

  setUp(new JmapReadOnlyScenario().generate(feeder, Configuration.ScenarioDuration)
    .inject(UsersTotal(Configuration.UserCount).injectDuring(Configuration.InjectionDuration))
    .protocols(HttpSettings.httpProtocol))

}
