package org.apache.james.gatling

import java.net.URL

import org.apache.james.gatling.control.{JamesWebAdministration, User}
import org.slf4j.{Logger, LoggerFactory}
import org.testcontainers.containers.GenericContainer

object JamesServer {

  private val imapPort = 80
  private val webadminPort = 8000
  private val logger: Logger = LoggerFactory.getLogger(JamesServer.getClass)

  class RunningServer(container: GenericContainer[_]) {
    lazy val mappedJmapPort: Integer = container.getMappedPort(imapPort)
    lazy val mappedWebadminPort: Integer = container.getMappedPort(webadminPort)
    private lazy val administration = new JamesWebAdministration(new URL(s"http://localhost:$webadminPort"))

    def addUser(user: User): Unit = administration.addUser(user)

    def stop(): Unit = container.stop()
  }

  def start(): RunningServer = {
    val james = new GenericContainer("linagora/james-memory")
    james.addExposedPorts(80, 8000)
    james.start()
    new RunningServer(james)
  }

}
