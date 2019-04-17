package org.apache.james.gatling

import java.net.URL

import org.apache.james.gatling.control.{Domain, JamesWebAdministration, User}
import org.slf4j.{Logger, LoggerFactory}
import org.testcontainers.containers.GenericContainer

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

    def addUser(user: User): Unit = {
      Await.result(administration.addUser(user), WAIT_TIMEOUT)
    }

    def addDomain(domain: Domain): Unit = Await.result(administration.addDomain(domain), WAIT_TIMEOUT)

    def importMessages() = {
      val emlPath = getClass.getResource("/message.eml").getPath
      val shPath = getClass.getResource("/add_mail.sh").getPath

      execCommand(s"docker cp  $emlPath $containerId:/message.eml")
      execCommand(s"docker cp $shPath $containerId:/add_mail.sh")
      execCommand(s"docker exec $containerId  /add_mail.sh")
    }

    private def execCommand(command: String) = {
      val rt = Runtime.getRuntime
      val copyPr = rt.exec(command)
      copyPr.waitFor()
    }

    private def containerId: String = container.getContainerId

    def stop(): Unit = container.stop()

  }

  def start(): RunningServer = {
    val james = new GenericContainer("linagora/james-memory")
    james.addExposedPorts(25, 80, 8000)
    james.start()
    new RunningServer(james)
  }

}
