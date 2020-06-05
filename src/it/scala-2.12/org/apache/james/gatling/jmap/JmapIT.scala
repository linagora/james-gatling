package org.apache.james.gatling.jmap

import java.net.URL

import io.gatling.core.Predef._
import io.gatling.core.funspec.GatlingFunSpec
import io.gatling.core.protocol.Protocol
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.Fixture.{bart, simpsonDomain}
import org.apache.james.gatling.JamesServer
import org.apache.james.gatling.JamesServer.RunningServer
import org.apache.james.gatling.control.AuthenticatedUserFeeder.AuthenticatedUserFeederBuilder
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
  lazy val protocolConf: Protocol = http.baseUrl(baseJamesJmap)
  private lazy val jamesJmap: JamesJmap = new JamesJmap(new URL(baseJamesJmap))

  protected def mappedJmapPort = server.mappedJmapPort

  protected lazy val users = List(bart)

  before {
    server.addDomain(simpsonDomain)
    users.foreach(server.addUser)
  }

  after {
    server.stop()
  }

  protected def scenario(scenarioFromFeeder: (UserFeederBuilder, RecipientFeederBuilder) => ScenarioBuilder) = {
    val userFeeder = UserFeeder.toFeeder(users)
    val recipientFeeder = RecipientFeeder.usersToFeeder(users)
    scenarioFromFeeder(userFeeder, recipientFeeder).actionBuilders.reverse.foreach { actionBuilder =>
      spec(actionBuilder)
    }
  }

  protected def scenario(scenarioFromFeeder: AuthenticatedUserFeederBuilder => ScenarioBuilder) = {
    val authenticatedUsers : Iterator[AuthenticatedUser] = users.view.map(user => Await.result(jamesJmap.authenticateUser(user), 5 seconds)).toIterator
    val userFeeder = AuthenticatedUserFeeder.toFeeder(authenticatedUsers)
    scenarioFromFeeder(userFeeder).actionBuilders.reverse.foreach { actionBuilder =>
      spec(actionBuilder)
    }
  }
}
