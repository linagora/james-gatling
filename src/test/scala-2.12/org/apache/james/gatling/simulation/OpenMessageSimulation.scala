package org.apache.james.gatling.simulation

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.scenari.{JmapOpenArbitraryMessageScenario, OpenMessage}

import scala.concurrent.duration._

class OpenMessageSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new JmapOpenArbitraryMessageScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(OpenMessage, 500 millis))

}
