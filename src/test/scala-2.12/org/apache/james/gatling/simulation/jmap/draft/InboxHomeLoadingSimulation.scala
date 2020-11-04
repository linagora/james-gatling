package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.InboxHomeLoading
import org.apache.james.gatling.jmap.draft.scenari.JmapInboxHomeLoadingScenario
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import scala.concurrent.duration._

class InboxHomeLoadingSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new JmapInboxHomeLoadingScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))

}
