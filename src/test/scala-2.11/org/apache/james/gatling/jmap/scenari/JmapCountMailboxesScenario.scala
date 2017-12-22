package org.apache.james.gatling.jmap.scenari

import scala.concurrent.duration.DurationInt

import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.JmapMailboxes
import org.apache.james.gatling.jmap.JmapMailboxes.numberOfSystemMailboxes
import org.apache.james.gatling.jmap.scenari.common.CommonSteps
import org.apache.james.gatling.jmap.scenari.common.Configuration.BaseJamesWebAdministrationUrl
import org.apache.james.gatling.jmap.scenari.common.Configuration.ScenarioDuration
import org.apache.james.gatling.jmap.scenari.common.Configuration.UserCount
import org.apache.james.gatling.jmap.scenari.common.HttpSettings
import org.apache.james.gatling.utils.RetryAuthentication.execWithRetryAuthentication

import io.gatling.core.Predef.Simulation
import io.gatling.core.Predef.atOnceUsers
import io.gatling.core.Predef.scenario
import io.gatling.core.session.Session
import io.gatling.commons.validation.Validation
import io.gatling.commons.validation.Success

/*
 * The aim of the scenario is to count the number of mailboxes.
 * No mailboxes are created manually, only the system mailboxes whose are automatically provisioned should be present.
 */
class JmapCountMailboxesScenario extends Simulation {

  val users = new UserCreator(BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(UserCount)
  
  val scn = scenario("JMAP scenario counting system mailboxes")
    .exec(CommonSteps.authentication(users))
    .exec(execWithRetryAuthentication(JmapMailboxes.getMailboxes, JmapMailboxes.storeMailboxIds))
    .exec((session: Session) => {
      val mailboxIds = session("mailboxIds").as[Vector[String]].map(x =>s"""$x""").mkString(", ")
      println(mailboxIds)
      Success.apply(session)
    })
    .during(ScenarioDuration) {
      execWithRetryAuthentication(JmapMailboxes.getMailboxes, JmapMailboxes.checkMailboxIdsHasNotChange)
        .pause(1 second , 2 seconds)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(HttpSettings.httpProtocol)
}
