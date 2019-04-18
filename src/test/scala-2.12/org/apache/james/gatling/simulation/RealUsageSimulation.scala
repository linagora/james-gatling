package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.core.assertion.AssertionWithPathAndTarget
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.{Password, User, UserFeeder, Username}
import org.apache.james.gatling.jmap.scenari.{InboxHomeLoading, JmapInboxHomeLoadingScenario, RealUsageScenario}
import org.apache.james.gatling.simulation.Injection._

import scala.concurrent.duration._

class RealUsageSimulation extends Simulation {

  private val NB_USERS = 100

  private val getUsers: Seq[User] = (0 until NB_USERS).map(i => User(Username(s"user$i@open-paas.org"), Password("secret")))

  private val feeder: SourceFeederBuilder[String] = UserFeeder.toFeeder(getUsers).circular

  private def buildMaxScenarioResponseTimeAssertion(scenario: RealUsageScenario, maxResponseTime: Duration) = {
    val _99Percentile: AssertionWithPathAndTarget[Int] = details(scenario.name).responseTime.percentile4
    _99Percentile.lt(maxResponseTime.toMillis.toInt)
  }

  setUp(new JmapInboxHomeLoadingScenario().generate(feeder)
    .inject(constantUserPerHour(50000) during 1.hour)
    .protocols(HttpSettings.httpProtocol))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))

}
