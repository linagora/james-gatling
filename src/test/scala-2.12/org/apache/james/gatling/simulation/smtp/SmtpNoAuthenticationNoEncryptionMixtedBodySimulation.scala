package org.apache.james.gatling.simulation.smtp

import org.apache.james.gatling.jmap.scenari.InboxHomeLoading
import org.apache.james.gatling.simulation.{Configuration, Injection, SimulationOnMailCorpus}
import org.apache.james.gatling.smtp.scenari.{SmtpNoAuthenticationNoEncryptionBigBodyScenario, SmtpNoAuthenticationNoEncryptionScenario}

import io.gatling.core.Predef._

import scala.concurrent.duration._

class SmtpNoAuthenticationNoEncryptionMixtedBodySimulation extends Simulation with SimulationOnMailCorpus {
  private val concurrentUsers = Injection.toUsersPerHour(Configuration.UserCount / 2).toInt
  private val scenarioSmallBody = new SmtpNoAuthenticationNoEncryptionScenario()
  private val scenarioBigBody = new SmtpNoAuthenticationNoEncryptionBigBodyScenario()

  setUp(injectUsersInScenario(scenarioSmallBody.generate(Configuration.ScenarioDuration, feeder), concurrentUsers),
        injectUsersInScenario(scenarioBigBody.generate(Configuration.ScenarioDuration, feeder), concurrentUsers))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))
}

