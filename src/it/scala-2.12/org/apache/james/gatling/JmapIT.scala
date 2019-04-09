package org.apache.james.gatling

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.funspec.GatlingFunSpec
import io.gatling.core.protocol.Protocol
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.Fixture.bart
import org.apache.james.gatling.JamesServer.RunningServer
import org.apache.james.gatling.control.UserFeeder
import org.apache.james.gatling.jmap.scenari.JmapAuthenticationScenario
import org.apache.james.gatling.{Fixture, JamesServer}
import org.slf4j
import org.slf4j.LoggerFactory

abstract class JmapIT extends GatlingFunSpec {
  val logger: slf4j.Logger = LoggerFactory.getLogger(this.getClass.getCanonicalName)

  private val server: RunningServer = JamesServer.start()
  lazy val protocolConf: Protocol = http.baseUrl(s"http://localhost:${server.mappedJmapPort}")
  before(server.addUser(bart))
  after(server.stop())

  protected def scenario(scenario: FeederBuilder => ScenarioBuilder) = {
    scenario(UserFeeder.toFeeder(Seq(bart))).actionBuilders.reverse.foreach(
      spec _
    )
  }
}

class JmapAuthenticationScenarioIT extends JmapIT {
  scenario(feederBuilder => new JmapAuthenticationScenario().generate().feed(feederBuilder))
}
