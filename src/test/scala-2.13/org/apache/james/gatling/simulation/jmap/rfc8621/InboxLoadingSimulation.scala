package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.InboxHomeLoading
import org.apache.james.gatling.jmap.rfc8621.scenari.InboxLoadingScenario
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import scala.concurrent.duration._

class InboxLoadingSimulation extends Simulation with SimulationOnMailCorpus {

  setUp(injectUsersInScenario(new InboxLoadingScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))

}
