package org.apache.james.gatling.simulation.jmap

import org.apache.james.gatling.jmap.scenari.{InboxHomeLoading, JmapInboxHomeLoadingScenario}
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._

class InboxHomeLoadingSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new JmapInboxHomeLoadingScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))

}
