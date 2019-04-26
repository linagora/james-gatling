package org.apache.james.gatling

import java.net.URL

import courier.{Envelope, Mailer, Text}
import javax.mail.internet.InternetAddress
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

    def addUser(user: User): Unit = Await.result(administration.addUser(user), WAIT_TIMEOUT)

    def addDomain(domain: Domain): Unit = Await.result(administration.addDomain(domain), WAIT_TIMEOUT)

    private def userToInternetAddress(user: User) = {
      new InternetAddress(user.username.value)
    }

    private val MAIL_SUBJECT = "D'oh!"
    private val MAIL_CONTENT = "Trying is the first step towards failure"
    private val mailerBuilder = Mailer("localhost", container.getMappedPort(587))

    def sendMessage(from: User)(to: User): Unit = {
      import courier.Defaults._
      val mailer = mailerBuilder.as(from.username.value, from.password.value)()
      Await.result(mailer(Envelope.from(userToInternetAddress(from))
        .to(userToInternetAddress(to))
        .subject(MAIL_SUBJECT)
        .content(Text(MAIL_CONTENT))), 1 second)
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
