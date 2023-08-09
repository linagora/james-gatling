package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.AuthenticatedUserFeeder._
import org.apache.james.gatling.control._
import org.apache.james.gatling.jmap.draft.scenari.JmapReadOnlyScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersTotal}

class JmapReadOnlySimulation extends Simulation {

  private def recordValueToString(recordValue: Any): String = recordValue match {
    case s: String => s
    case a: Any => println("Warning: calling toString on a feeder value"); a.toString
  }

  val authenticatedUsers: Seq[AuthenticatedUser] = csv("usersAuthenticated.csv").readRecords
    .map(record =>
      AuthenticatedUser(
        username = Username(recordValueToString(record("username"))),
        accessToken = BearerAccessToken(recordValueToString(record("bearerAccessToken")))))

  val feeder: AuthenticatedUserFeeder = AuthenticatedUserFeeder.toFeeder(authenticatedUsers.toIterator)

  setUp(new JmapReadOnlyScenario().generate(feeder, Configuration.ScenarioDuration)
    .inject(UsersTotal(authenticatedUsers.length).injectDuring(Configuration.InjectionDuration))
    .protocols(HttpSettings.httpProtocol))

}
