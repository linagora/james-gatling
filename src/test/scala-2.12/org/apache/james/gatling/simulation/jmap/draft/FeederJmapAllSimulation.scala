package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control._
import org.apache.james.gatling.jmap.draft.RecipientAddress
import org.apache.james.gatling.jmap.draft.scenari.JmapAllScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

class FeederJmapAllSimulation extends Simulation {

  private def recordValueToString(recordValue: Any): String = recordValue match {
    case s: String => s
    case a: Any => println("Warning: calling toString on a feeder value"); a.toString
  }

  private val users: Seq[User] = csv("users.csv").readRecords
    .map(record =>
      User(
        username = Username(recordValueToString(record("username"))),
        password = Password(recordValueToString(record("password")))))

  private val scenario = new JmapAllScenario()

  private val recipients: Seq[RecipientAddress] = csv("users.csv").readRecords
    .map(record =>
      RecipientAddress(recordValueToString(record("username"))))

  setUp(scenario
    .generate(UserFeeder.toFeeder(users).circular, Configuration.ScenarioDuration, RecipientFeeder.toFeeder(recipients).random)
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol)
}