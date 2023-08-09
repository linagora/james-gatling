package org.apache.james.gatling.simulation.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.RecipientFeeder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.rfc8621.SessionStep.retrieveAccountId
import org.apache.james.gatling.jmap.rfc8621.scenari.{EmailKeywordsUpdatesScenario, InboxLoadingScenario, OpenEmailScenario, SelectMailboxScenario}
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapMailbox}
import org.apache.james.gatling.jmap.{InboxHomeLoading, OpenMessage, SelectMailbox}
import org.apache.james.gatling.simulation.{SimulationOnMailCorpus, UsersPerSecond}

import scala.concurrent.duration._

class AllSimulation extends Simulation with SimulationOnMailCorpus {
  private val MIN_MESSAGES_IN_MAILBOXES_TO_SELECT = 100

  def emailSubmissionScenario(userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder): ScenarioBuilder = scenario("EmailSubmissionScenario")
    .feed(userFeeder)
    .exec(retrieveAccountId)
    .exec(JmapMailbox.provisionSystemMailboxes())
    .exec(JmapEmail.submitEmails(recipientFeeder))

  setUp(
      injectUsersInScenario(new InboxLoadingScenario().generate(feeder), UsersPerSecond(0.5)),
      injectUsersInScenario(new OpenEmailScenario().generate(feeder), UsersPerSecond(0.5)),
      injectUsersInScenario(new SelectMailboxScenario(MIN_MESSAGES_IN_MAILBOXES_TO_SELECT).generate(feeder), UsersPerSecond(0.5)),
      injectUsersInScenario(new EmailKeywordsUpdatesScenario().generate(feeder), UsersPerSecond(0.5)),
      injectUsersInScenario(emailSubmissionScenario(feeder, RecipientFeeder.usersToFeeder(getUsers)), UsersPerSecond(0.5)))
    .assertions(
      buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds),
      buildMaxScenarioResponseTimeAssertion(OpenMessage, 1 second),
      buildMaxScenarioResponseTimeAssertion(SelectMailbox, 2 second))
}