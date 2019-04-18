package org.apache.james.gatling

import java.net.URL

import org.apache.james.gatling.control.{Domain, JamesWebAdministration, User}
import org.slf4j.{Logger, LoggerFactory}
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.MountableFile

import scala.concurrent.Await
import scala.concurrent.duration._

object JamesServer {

  private val WAIT_TIMEOUT = 30 seconds
  private val jmapPort = 80
  private val webadminPort = 8000
  private val logger: Logger = LoggerFactory.getLogger(JamesServer.getClass)

  class RunningServer(container: GenericContainer[_]) {
    lazy val mappedJmapPort: Integer = container.getMappedPort(jmapPort)
    lazy val mappedWebadminPort: Integer = container.getMappedPort(webadminPort)
    lazy val mappedSmptPort: Integer = container.getMappedPort(587)

    private lazy val administration = new JamesWebAdministration(new URL(s"http://localhost:$mappedWebadminPort"))

    def addUser(user: User): Unit = Await.result(administration.addUser(user), WAIT_TIMEOUT)

    def addDomain(domain: Domain): Unit = Await.result(administration.addDomain(domain), WAIT_TIMEOUT)

    def importMessages(from: User)(to: User): Unit = {
      container.copyFileToContainer(MountableFile.forClasspathResource("/message.eml"), "/message.eml")
      container.execInContainer(
        "curl",
        "--url", "smtp://localhost:587",
        "--mail-from", from.username.value,
        "--mail-rcpt", to.username.value,
        "--upload-file", "/message.eml",
        "--user", s"${from.username.value}:${from.password.value}",
        "--insecure")
    }

    def stop(): Unit = container.stop()

  }

  def start(): RunningServer = {
    val james = new GenericContainer("linagora/james-memory")
    james.addExposedPorts(25, 80, 8000)
    james.start()
    new RunningServer(james)
  }

}
