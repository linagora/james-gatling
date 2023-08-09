package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.{InboxHomeLoading, OpenMessage, SelectMailbox}
import org.apache.james.gatling.jmap.draft.scenari.{JmapInboxHomeLoadingScenario, JmapOpenArbitraryMessageScenario, JmapSelectArbitraryMailboxScenario}
import org.apache.james.gatling.simulation.{SimulationOnMailCorpus, UsersPerHour}

import scala.concurrent.duration._

class RealUsageSimulation extends Simulation with SimulationOnMailCorpus {
  private val MIN_MESSAGES_IN_MAILBOXES_TO_SELECT = 100
  setUp(
    injectUsersInScenario(new JmapInboxHomeLoadingScenario().generate(feeder), UsersPerHour(600)),
    injectUsersInScenario(new JmapOpenArbitraryMessageScenario().generate(feeder), UsersPerHour(600)),
    injectUsersInScenario(new JmapSelectArbitraryMailboxScenario(MIN_MESSAGES_IN_MAILBOXES_TO_SELECT).generate(feeder), UsersPerHour(600))
  ).assertions(
    buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds),
    buildMaxScenarioResponseTimeAssertion(OpenMessage, 500 millis),
    buildMaxScenarioResponseTimeAssertion(SelectMailbox, 1 second)
  )

}
