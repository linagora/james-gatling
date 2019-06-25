package org.apache.james.gatling.smtp

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.funspec.GatlingFunSpec
import io.gatling.core.protocol.Protocol
import io.gatling.core.structure.ScenarioBuilder
import org.apache.james.gatling.Fixture.{bart, simpsonDomain}
import org.apache.james.gatling.JamesServer
import org.apache.james.gatling.JamesServer.RunningServer
import org.apache.james.gatling.control.UserFeeder
import org.slf4j
import org.slf4j.LoggerFactory

abstract class SmtpIT extends GatlingFunSpec {
  protected val logger: slf4j.Logger = LoggerFactory.getLogger(this.getClass.getCanonicalName)

  protected val server: RunningServer = JamesServer.start()
  lazy val protocolConf: Protocol = new SmtpProtocol("localhost", false, mappedSmtpPort, false)

  protected def mappedSmtpPort = server.mappedSmtpPort

  protected lazy val users = List(bart)

  before {
    server.addDomain(simpsonDomain)
    users.foreach(server.addUser)
  }

  after {
    server.stop()
  }

  protected def scenario(scenarioFromFeeder: FeederBuilder => ScenarioBuilder) = {
    val feeder = UserFeeder.toFeeder(users)
    scenarioFromFeeder(feeder).actionBuilders.reverse.foreach { actionBuilder =>
      spec(actionBuilder)
    }
  }
}

