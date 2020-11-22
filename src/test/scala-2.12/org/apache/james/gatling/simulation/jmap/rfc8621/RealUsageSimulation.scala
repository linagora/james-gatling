package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.jmap.rfc8621.scenari.{InboxLoadingScenario, OpenEmailScenario, SelectMailboxScenario}
import org.apache.james.gatling.jmap.{InboxHomeLoading, OpenMessage, SelectMailbox}
import org.apache.james.gatling.simulation.{SimulationOnMailCorpus, UsersPerHour}

import scala.concurrent.duration._

class RealUsageSimulation extends Simulation with SimulationOnMailCorpus {
  private val MIN_MESSAGES_IN_MAILBOXES_TO_SELECT = 100

  setUp(
      injectUsersInScenario(new InboxLoadingScenario().generate(feeder), UsersPerHour(600)),
      injectUsersInScenario(new OpenEmailScenario().generate(feeder), UsersPerHour(600)),
      injectUsersInScenario(new SelectMailboxScenario(MIN_MESSAGES_IN_MAILBOXES_TO_SELECT).generate(feeder), UsersPerHour(600)))
  .assertions(
      buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds),
      buildMaxScenarioResponseTimeAssertion(OpenMessage, 1 second),
      buildMaxScenarioResponseTimeAssertion(SelectMailbox, 2 second))
}
