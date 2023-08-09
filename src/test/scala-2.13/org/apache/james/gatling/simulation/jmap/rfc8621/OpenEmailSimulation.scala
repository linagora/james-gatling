package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.OpenMessage
import org.apache.james.gatling.jmap.rfc8621.scenari.OpenEmailScenario
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import scala.concurrent.duration._

class OpenEmailSimulation extends Simulation with SimulationOnMailCorpus {
  setUp(injectUsersInScenario(new OpenEmailScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(OpenMessage, 1 second))
}
