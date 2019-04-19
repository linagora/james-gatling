package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.core.assertion.AssertionWithPathAndTarget
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.{Password, User, UserFeeder, Username}
import org.apache.james.gatling.jmap.scenari.RealUsageScenario
import org.apache.james.gatling.simulation.Injection.constantUserPerHour

import scala.concurrent.duration._

trait SimulationOnMailCorpus {

  protected val NB_USERS = 100

  protected val getUsers: Seq[User] = (0 until NB_USERS).map(i => User(Username(s"user$i@open-paas.org"), Password("secret")))

  protected val feeder: SourceFeederBuilder[String] = UserFeeder.toFeeder(getUsers).circular

  protected def injectUsersInScenario(scenario: ScenarioBuilder, nbUsers: Int = 50000) = {
    scenario
      .inject(constantUserPerHour(nbUsers) during 1.hour)
      .protocols(HttpSettings.httpProtocol)
  }


  protected def buildMaxScenarioResponseTimeAssertion(scenario: RealUsageScenario, maxResponseTime: Duration) = {
    val _99Percentile: AssertionWithPathAndTarget[Int] = details(scenario.name).responseTime.percentile4
    _99Percentile.lt(maxResponseTime.toMillis.toInt)
  }
}
