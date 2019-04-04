package org.apache.james.gatling.simulation.jmap

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{Password, User, Username}
import org.apache.james.gatling.jmap.scenari.FeederJmapAllScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

import scala.concurrent.Future

class FeederJmapAllSimulation extends Simulation {

  private def recordValueToString(recordValue: Any):String = recordValue match {
    case s: String => s
    case a: Any => println("Warning: calling toString on a feeder value"); a.toString
  }

  private val users = csv("users.csv").readRecords
    .map({ record => User(Username(recordValueToString(record("username"))), Password(recordValueToString(record("password")))) })
    .map(Future.successful(_))
    .seq

  private val scenario = new FeederJmapAllScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, users).inject(atOnceUsers(Configuration.UserCount))).protocols(HttpSettings.httpProtocol)
}