package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.{Password, User, UserFeeder, Username}
import org.apache.james.gatling.jmap.scenari.{InboxHomeLoading, JmapInboxHomeLoadingScenario, RealUsageScenario}
import org.apache.james.gatling.simulation.utils.UsageFrequency
import org.slf4j
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

class RealUsageSimulation extends Simulation {
  private val logger: slf4j.Logger = LoggerFactory.getLogger(this.getClass.getCanonicalName)

  private val NB_USERS = 100

  private val getUsers: Seq[User] = (0 until NB_USERS).map(i => User(Username(s"user$i@open-paas.org"), Password("secret")))

  private val feeder: SourceFeederBuilder[String] = UserFeeder.toFeeder(getUsers).circular

  private def buildPerfScenario(scenarioName: String, scenarioBuilder: ScenarioBuilder, usageFrequency: UsageFrequency) = scenario(scenarioName)
    .forever(
      pace(usageFrequency.paceFromFrequency).exec(scenarioBuilder)
    )
    .inject(rampUsers(NB_USERS) during (1 minute))
    .protocols(HttpSettings.httpProtocol)


  private def buildMaxScenarioResponseTimeAssertion(scenario: RealUsageScenario, maxResponseTime: Duration) = {
    details(scenario.name).responseTime.max.lt(maxResponseTime.toMillis.toInt)
  }

  private val inboxHomeLoadingScenario = {
    buildPerfScenario("Inbox home loading",
      new JmapInboxHomeLoadingScenario().generate(feeder),
      UsageFrequency.ONE_TIME_PER_USER_PER_HOUR_FOR_FIFTY_THOUSANDS_USERS
    )
  }
  setUp(inboxHomeLoadingScenario)
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))

}
