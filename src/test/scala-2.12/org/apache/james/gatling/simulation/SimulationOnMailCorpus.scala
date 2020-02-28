package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.core.assertion.AssertionWithPathAndTarget
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.control.{Password, User, UserFeeder, Username}
import org.apache.james.gatling.jmap.MailboxName
import org.apache.james.gatling.jmap.scenari.RealUsageScenario

import scala.concurrent.duration._

trait SimulationOnMailCorpus {

  protected val NB_USERS = 100

  protected val NB_ROOT_MAILBOXES = 10
  protected val NB_SUB_MAILBOXES = 10

  protected val getUsers: Seq[User] = (0 until NB_USERS).map(i => User(Username(s"user$i@open-paas.org"), Password("secret")))

  protected val getMailboxes: Seq[MailboxName] =
    (Seq("INBOX")
      ++ (0 until NB_ROOT_MAILBOXES).map(i => s"rmbx$i")
      ++ (0 until NB_ROOT_MAILBOXES).flatMap(rootIndex => (0 until NB_SUB_MAILBOXES).map(subIndex => s"rmbx$rootIndex.smbx$subIndex")))
    .map(new MailboxName(_))

  protected val feeder: SourceFeederBuilder[String] = UserFeeder.toFeeder(getUsers).circular

  protected def injectUsersInScenario(scenario: ScenarioBuilder, nbUsers: UsersDensity = UsersPerHour(50000)) = {
    scenario
      .inject(nbUsers.injectDuring(Configuration.InjectionDuration))
      .protocols(HttpSettings.httpProtocol)
  }


  protected def buildMaxScenarioResponseTimeAssertion(scenario: RealUsageScenario, maxResponseTime: Duration) = {
    val _99Percentile: AssertionWithPathAndTarget[Int] = details(scenario.name).responseTime.percentile4
    _99Percentile.lt(maxResponseTime.toMillis.toInt)
  }
}
