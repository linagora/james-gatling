package org.apache.james.gatling.simulation.jmap.draft

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.RecipientFeeder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.scenari.{JmapInboxHomeLoadingScenario, JmapMessageFlagUpdatesScenario, JmapOpenArbitraryMessageScenario, JmapSelectArbitraryMailboxScenario}
import org.apache.james.gatling.jmap.draft.{CommonSteps, JmapMessages}
import org.apache.james.gatling.jmap.{InboxHomeLoading, OpenMessage, SelectMailbox}
import org.apache.james.gatling.simulation.{SimulationOnMailCorpus, UsersPerSecond}

import scala.concurrent.duration._

class JmapAllSimulation extends Simulation with SimulationOnMailCorpus {
  private val MIN_MESSAGES_IN_MAILBOXES_TO_SELECT = 100

  def sendMessageScenario(userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder =
    scenario("JmapSendMessages")
      .feed(userFeeder)
      .exec(CommonSteps.provisionSystemMailboxes())
      .exec(JmapMessages.sendMessagesToUserWithRetryAuthentication(recipientFeeder))

  setUp(
      injectUsersInScenario(new JmapInboxHomeLoadingScenario().generate(feeder), UsersPerSecond(0.5)),
      injectUsersInScenario(new JmapOpenArbitraryMessageScenario().generate(feeder), UsersPerSecond(0.5)),
      injectUsersInScenario(new JmapSelectArbitraryMailboxScenario(MIN_MESSAGES_IN_MAILBOXES_TO_SELECT).generate(feeder), UsersPerSecond(0.5)),
      injectUsersInScenario(new JmapMessageFlagUpdatesScenario().generate(feeder), UsersPerSecond(0.5)),
      injectUsersInScenario(sendMessageScenario(feeder, RecipientFeeder.usersToFeeder(getUsers)), UsersPerSecond(0.5)))
    .assertions(
      buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds),
      buildMaxScenarioResponseTimeAssertion(OpenMessage, 1 second),
      buildMaxScenarioResponseTimeAssertion(SelectMailbox, 2 second))
}