package org.apache.james.gatling.simulation

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.scenari.{InboxHomeLoading, JmapInboxHomeLoadingScenario}

import scala.concurrent.duration._

class InboxHomeLoadingSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new JmapInboxHomeLoadingScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))

}
