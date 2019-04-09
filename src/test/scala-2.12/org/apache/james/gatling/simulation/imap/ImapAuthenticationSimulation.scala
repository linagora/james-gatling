package org.apache.james.gatling.simulation.imap

import com.linagora.gatling.imap.PreDef.imap
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.imap.scenari.ImapAuthenticationScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

class ImapAuthenticationSimulation extends Simulation {

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)

  private val scenario = new ImapAuthenticationScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, users)
    .inject(atOnceUsers(Configuration.UserCount)))
    .protocols(HttpSettings.httpProtocol, imap.host(Configuration.ServerHostName).build())

}
