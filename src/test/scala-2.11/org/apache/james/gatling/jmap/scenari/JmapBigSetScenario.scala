package org.apache.james.gatling.jmap.scenari

import scala.concurrent.duration.DurationInt

import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.JmapMessages
import org.apache.james.gatling.jmap.scenari.common.CommonSteps
import org.apache.james.gatling.jmap.scenari.common.Configuration.BaseJamesWebAdministrationUrl
import org.apache.james.gatling.jmap.scenari.common.Configuration.ScenarioDuration
import org.apache.james.gatling.jmap.scenari.common.Configuration.UserCount
import org.apache.james.gatling.jmap.scenari.common.Configuration.NumberOfMailboxes
import org.apache.james.gatling.jmap.scenari.common.Configuration.NumberOfMessages
import org.apache.james.gatling.jmap.scenari.common.HttpSettings
import org.apache.james.gatling.utils.RetryAuthentication.execWithRetryAuthentication

import io.gatling.core.Predef.Simulation
import io.gatling.core.Predef.atOnceUsers
import io.gatling.core.Predef.scenario
import org.apache.james.gatling.jmap.JmapMailboxes
import org.apache.james.gatling.jmap.JmapMailboxes.numberOfSystemMailboxes
import org.apache.james.gatling.jmap.Id
import org.apache.james.gatling.jmap.Name

/*
 * The aim of the scenario is to provide multiple mailboxes per user and several mails in them.
 * Then, it's just checking that the created mailboxes exists.
 */
class JmapBigSetScenario extends Simulation {

  val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)
  
  val scn = scenario("JMAP scenario on multiple mailboxes containing multiple messages")
    .exec(CommonSteps.provisionUsersWithMailboxesAndMessages(users, NumberOfMailboxes, NumberOfMessages))
    .during(ScenarioDuration) {
      execWithRetryAuthentication(JmapMailboxes.getMailboxes, JmapMailboxes.getMailboxesChecks(numberOfMailboxesPerUser))
        .exec(execWithRetryAuthentication(JmapMessages.listMessages, JmapMessages.listMessagesChecks))
        .pause(1 second , 2 seconds)
    }

  def numberOfMailboxesPerUser: Int =
    NumberOfMailboxes + numberOfSystemMailboxes
    
  setUp(scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)
}
