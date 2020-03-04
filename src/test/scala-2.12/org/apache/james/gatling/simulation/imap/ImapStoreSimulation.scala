package org.apache.james.gatling.simulation.imap

import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.imap.scenari.ImapStoreScenario
import org.apache.james.gatling.jmap.scenari.InboxHomeLoading
import org.apache.james.gatling.simulation.{Configuration, HttpSettings, SimulationOnMailCorpus}

import com.linagora.gatling.imap.PreDef.imap

import scala.concurrent.duration._

class ImapStoreSimulation extends Simulation with SimulationOnMailCorpus {
  setUp(injectUsersInScenario(new ImapStoreScenario().generate(Configuration.ScenarioDuration, feeder)))
    .protocols(HttpSettings.httpProtocol, imap.host(Configuration.ImapServerHostName).build())
    .assertions(buildMaxScenarioResponseTimeAssertion(InboxHomeLoading, 2 seconds))
}
