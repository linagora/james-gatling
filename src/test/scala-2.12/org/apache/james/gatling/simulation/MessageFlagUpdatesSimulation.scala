package org.apache.james.gatling.simulation

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.scenari.{JmapMessageFlagUpdatesScenario, MessageFlagUpdates}

import scala.concurrent.duration._

class MessageFlagUpdatesSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new JmapMessageFlagUpdatesScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(MessageFlagUpdates, 1 second))

}
