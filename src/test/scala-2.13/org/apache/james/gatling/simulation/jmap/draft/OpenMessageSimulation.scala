package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.OpenMessage
import org.apache.james.gatling.jmap.draft.scenari.JmapOpenArbitraryMessageScenario
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import scala.concurrent.duration._

class OpenMessageSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new JmapOpenArbitraryMessageScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(OpenMessage, 500 millis))

}
