package org.apache.james.gatling.jmap.rfc8621

import java.net.URI

import io.gatling.core.Predef._
import io.gatling.core.funspec.GatlingFunSpec
import io.gatling.core.protocol.Protocol
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.Fixture.{bart, simpsonDomain}
import org.apache.james.gatling.JamesServer
import org.apache.james.gatling.JamesServer.RunningServer
import org.apache.james.gatling.control.AuthenticatedUserFeeder.AuthenticatedUserFeeder
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.control.{AuthenticatedUser, AuthenticatedUserFeeder, JamesJmap, RecipientFeeder, UserFeeder}
import org.slf4j
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._

abstract class JmapIT extends GatlingFunSpec {
  protected val logger: slf4j.Logger = LoggerFactory.getLogger(this.getClass.getCanonicalName)

  protected val server: RunningServer = JamesServer.start()
  private val baseJamesJmap: String = s"http://localhost:${server.mappedJmapPort}"
  private val baseJamesWebsocket: String = s"ws://localhost:${server.mappedJmapPort}"
  lazy val protocolConf: Protocol = http.baseUrl(baseJamesJmap)
    .wsBaseUrl(baseJamesWebsocket)
  private lazy val jamesJmap: JamesJmap = new JamesJmap(new URI(baseJamesJmap).toURL)

  protected def mappedJmapPort = server.mappedJmapPort

  protected lazy val users = List(bart)

  before {
    server.addDomain(simpsonDomain)
    users.foreach(server.addUser)
  }

  after {
    server.stop()
  }

  // ponytail: gatling 3.15 made ScenarioBuilder.actionBuilders private[core], so the
  // whole scenario is registered as a single Executable instead of extracting each action.
  protected def scenario(scenarioFromFeeder: (UserFeederBuilder, RecipientFeederBuilder) => ScenarioBuilder) = {
    spec(scenarioFromFeeder(UserFeeder.toFeeder(users), RecipientFeeder.usersToFeeder(users)))
  }

  protected def scenario(scenarioFromFeeder: AuthenticatedUserFeeder => ScenarioBuilder) = {
    val authenticatedUsers : Iterator[AuthenticatedUser] = users.view.map(user => Await.result(jamesJmap.authenticateUser(user), 5 seconds)).iterator
    spec(scenarioFromFeeder(AuthenticatedUserFeeder.toFeeder(authenticatedUsers)))
  }
}
