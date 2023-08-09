package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.imap.scenari.ImapListMessagesBodyStructureScenario
import org.apache.james.gatling.jmap.ListMessageBodyStructure
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, SimulationOnMailCorpus, UsersPerHour}

import scala.concurrent.duration._

class ImapListMessagesBodyStructureSimulation extends Simulation with SimulationOnMailCorpus {
  private val MAILS_FETCHED = 50

  setUp(injectUsersInScenario(new ImapListMessagesBodyStructureScenario().generate(feeder, getMailboxes, MAILS_FETCHED), UsersPerHour(2500)))
    .protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build())
    .assertions(buildMaxScenarioResponseTimeAssertion(ListMessageBodyStructure, 2 seconds))
}
