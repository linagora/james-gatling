package org.apache.james.gatling.simulation.imap

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.imap.scenari.ImapListMessagesBodyStructureScenario
import org.apache.james.gatling.jmap.scenari.ListMessageBodyStructure
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, SimulationOnMailCorpus}

import com.linagora.gatling.imap.PreDef.imap

import scala.concurrent.duration._

class ImapListMessagesBodyStructureSimulation extends Simulation with SimulationOnMailCorpus {
  private val MAILS_FECTHED = 200
  setUp(injectUsersInScenario(new ImapListMessagesBodyStructureScenario().generate(feeder, getMailboxes, MAILS_FECTHED)))
    .protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build())
    .assertions(buildMaxScenarioResponseTimeAssertion(ListMessageBodyStructure, 2 seconds))
}
