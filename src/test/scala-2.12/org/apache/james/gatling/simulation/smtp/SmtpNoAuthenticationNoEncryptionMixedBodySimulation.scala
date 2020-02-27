package org.apache.james.gatling.simulation.smtp

import org.apache.james.gatling.jmap.scenari.InboxHomeLoading
import org.apache.james.gatling.simulation.{Configuration, UsersPerSecond, SimulationOnMailCorpus}
import org.apache.james.gatling.smtp.scenari.{SmtpNoAuthenticationNoEncryptionBigBodyScenario, SmtpNoAuthenticationNoEncryptionScenario}

import io.gatling.core.Predef._

import scala.concurrent.duration._

class SmtpNoAuthenticationNoEncryptionMixedBodySimulation extends Simulation with SimulationOnMailCorpus {
  private val concurrentUsers = UsersPerSecond(Configuration.UserCount / 2)
  private val scenarioSmallBody = new SmtpNoAuthenticationNoEncryptionScenario()
  private val scenarioBigBody = new SmtpNoAuthenticationNoEncryptionBigBodyScenario()

  setUp(injectUsersInScenario(scenarioSmallBody.generate(Configuration.ScenarioDuration, feeder), concurrentUsers),
        injectUsersInScenario(scenarioBigBody.generate(Configuration.ScenarioDuration, feeder), concurrentUsers))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))
}

