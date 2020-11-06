package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.InboxHomeLoading
import org.apache.james.gatling.jmap.rfc8621.scenari.EmailKeywordsUpdatesScenario
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import scala.concurrent.duration.DurationInt

class EmailKeywordsUpdatesSimulation extends Simulation with SimulationOnMailCorpus {
  setUp(injectUsersInScenario(new EmailKeywordsUpdatesScenario().generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))
}
