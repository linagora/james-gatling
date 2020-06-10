package org.apache.james.gatling.simulation.jmap

import org.apache.james.gatling.control.AuthenticatedUserFeeder._
import org.apache.james.gatling.control._
import org.apache.james.gatling.jmap.scenari.JmapReadOnlyScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, UsersTotal}

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

class JmapReadOnlySimulation extends Simulation {

  private def recordValueToString(recordValue: Any): String = recordValue match {
    case s: String => s
    case a: Any => println("Warning: calling toString on a feeder value"); a.toString
  }

  val authenticatedUsers: Seq[AuthenticatedUser] = csv("usersAuthenticated.csv").readRecords
    .map(record =>
      AuthenticatedUser(
        username = Username(recordValueToString(record("username"))),
        jwtAccessToken = JwtAccessToken(recordValueToString(record("jwtAccessToken")))))

  val feeder: AuthenticatedUserFeederBuilder = AuthenticatedUserFeeder.toFeeder(authenticatedUsers.toIterator)

  setUp(new JmapReadOnlyScenario().generate(feeder, Configuration.ScenarioDuration)
    .inject(UsersTotal(authenticatedUsers.length).injectDuring(Configuration.InjectionDuration))
    .protocols(HttpSettings.httpProtocol))

}
