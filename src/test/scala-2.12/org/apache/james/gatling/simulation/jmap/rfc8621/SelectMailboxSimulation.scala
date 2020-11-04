package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.SelectMailbox
import org.apache.james.gatling.jmap.draft.scenari.JmapSelectArbitraryMailboxScenario
import org.apache.james.gatling.jmap.rfc8621.scenari.SelectMailboxScenario
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import scala.concurrent.duration._

class SelectMailboxSimulation extends Simulation with SimulationOnMailCorpus {
  private val MIN_MESSAGES_IN_MAILBOXES_TO_SELECT = 100

  setUp(injectUsersInScenario(new SelectMailboxScenario(MIN_MESSAGES_IN_MAILBOXES_TO_SELECT).generate(feeder)))
    .assertions(buildMaxScenarioResponseTimeAssertion(SelectMailbox, 1 second))

}
