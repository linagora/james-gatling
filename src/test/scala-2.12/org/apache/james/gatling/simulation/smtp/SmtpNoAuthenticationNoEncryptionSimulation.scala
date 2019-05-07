package org.apache.james.gatling.simulation.smtp


import io.gatling.core.Predef._
import org.apache.james.gatling.simulation.{Configuration, SimulationOnMailCorpus}
import org.apache.james.gatling.smtp.scenari.SmtpNoAuthenticationNoEncryptionScenario

class SmtpNoAuthenticationNoEncryptionSimulation extends Simulation with SimulationOnMailCorpus {
  setUp(injectUsersInScenario(new SmtpNoAuthenticationNoEncryptionScenario().generate(Configuration.ScenarioDuration, feeder)))
}
