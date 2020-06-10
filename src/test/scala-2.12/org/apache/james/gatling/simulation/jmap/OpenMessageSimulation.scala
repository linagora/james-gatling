package org.apache.james.gatling.simulation.jmap

import org.apache.james.gatling.jmap.scenari.{JmapOpenArbitraryMessageScenario, OpenMessage}
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._

class OpenMessageSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new JmapOpenArbitraryMessageScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(OpenMessage, 500 millis))

}
