package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.draft.scenari.{DefaultInboxSearchLoadingScenario, InboxHomeLoading}
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import scala.concurrent.duration._

class DefaultInboxSearchLoadingSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new DefaultInboxSearchLoadingScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 1 seconds))

}
