package org.apache.james.gatling.simulation.jmap

import org.apache.james.gatling.jmap.scenari.{DefaultInboxSearchLoadingScenario, InboxHomeLoading}
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._

class DefaultInboxSearchLoadingSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new DefaultInboxSearchLoadingScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 1 seconds))

}
