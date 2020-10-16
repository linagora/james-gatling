package org.apache.james.gatling.simulation.jmap

import org.apache.james.gatling.jmap.scenari.{JmapSelectArbitraryMailboxScenario, SelectMailbox}
import org.apache.james.gatling.simulation.SimulationOnMailCorpus

import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._

class SelectMailboxSimulation extends Simulation with SimulationOnMailCorpus {
  private val MIN_MESSAGES_IN_MAILBOXES_TO_SELECT = 100

  setUp(
    injectUsersInScenario(new JmapSelectArbitraryMailboxScenario(MIN_MESSAGES_IN_MAILBOXES_TO_SELECT).generate(feeder))
  ).assertions(buildMaxScenarioResponseTimeAssertion(SelectMailbox, 1 second))

}
