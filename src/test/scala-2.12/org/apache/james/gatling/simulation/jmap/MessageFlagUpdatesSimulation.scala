package org.apache.james.gatling.simulation.jmap

import org.apache.james.gatling.jmap.scenari.{JmapMessageFlagUpdatesScenario, MessageFlagUpdates}
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._

class MessageFlagUpdatesSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new JmapMessageFlagUpdatesScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(MessageFlagUpdates, 1 second))

}
