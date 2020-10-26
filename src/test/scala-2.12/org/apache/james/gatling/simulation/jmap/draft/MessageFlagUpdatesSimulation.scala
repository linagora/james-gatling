package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.draft.scenari.{JmapMessageFlagUpdatesScenario, MessageFlagUpdates}
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import scala.concurrent.duration._

class MessageFlagUpdatesSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new JmapMessageFlagUpdatesScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(MessageFlagUpdates, 1 second))

}
