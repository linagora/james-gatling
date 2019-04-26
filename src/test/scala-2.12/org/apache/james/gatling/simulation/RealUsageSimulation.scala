package org.apache.james.gatling.simulation

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.scenari._

import scala.concurrent.duration._

class RealUsageSimulation extends Simulation with SimulationOnMailCorpus {
  private val MIN_MESSAGES_IN_MAILBOXES_TO_SELECT = 100
  setUp(
    injectUsersInScenario(new JmapInboxHomeLoadingScenario().generate(feeder)),
    injectUsersInScenario(new JmapOpenArbitraryMessageScenario().generate(feeder)),
    injectUsersInScenario(new JmapSelectArbitraryMaiboxScenario(MIN_MESSAGES_IN_MAILBOXES_TO_SELECT).generate(feeder))
  ).assertions(
    buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds),
    buildMaxScenarioResponseTimeAssertion(OpenMessage, 500 millis),
    buildMaxScenarioResponseTimeAssertion(SelectMailbox, 1 second)
  )

}
