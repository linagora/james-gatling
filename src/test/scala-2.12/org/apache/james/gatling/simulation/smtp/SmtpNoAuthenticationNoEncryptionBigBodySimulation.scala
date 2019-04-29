package org.apache.james.gatling.simulation.smtp

import io.gatling.core.Predef._
import org.apache.james.gatling.jmap.scenari.InboxHomeLoading
import org.apache.james.gatling.simulation.{Configuration, SimulationOnMailCorpus}
import org.apache.james.gatling.smtp.scenari.SmtpNoAuthenticationNoEncryptionBigBodyScenario

import scala.concurrent.duration._

class SmtpNoAuthenticationNoEncryptionBigBodySimulation extends Simulation with SimulationOnMailCorpus {
  setUp(injectUsersInScenario(new SmtpNoAuthenticationNoEncryptionBigBodyScenario().generate(Configuration.ScenarioDuration, feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))
}
