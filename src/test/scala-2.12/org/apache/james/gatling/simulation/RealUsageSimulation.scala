package org.apache.james.gatling.simulation

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.scenari.{InboxHomeLoading, JmapInboxHomeLoadingScenario, JmapOpenArbitraryMessageScenario, OpenMessage}

import scala.concurrent.duration._

class RealUsageSimulation extends Simulation with SimulationOnMailCorpus {
  setUp(
    injectUsersInScenario(new JmapInboxHomeLoadingScenario().generate(feeder)),
    injectUsersInScenario(new JmapOpenArbitraryMessageScenario().generate(feeder))
  ).assertions(
    buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds),
    buildMaxScenarioResponseTimeAssertion(OpenMessage, 500 millis)
  )

}
