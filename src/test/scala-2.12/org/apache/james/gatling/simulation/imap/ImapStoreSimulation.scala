package org.apache.james.gatling.simulation.imap

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.imap.scenari.ImapStoreScenario
import org.apache.james.gatling.jmap.scenari.InboxHomeLoading
import org.apache.james.gatling.simulation.{Configuration, SimulationOnMailCorpus}

import scala.concurrent.duration._

class ImapStoreSimulation extends Simulation with SimulationOnMailCorpus {
  setUp(injectUsersInScenario(new ImapStoreScenario().generate(Configuration.ScenarioDuration, feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))
}
