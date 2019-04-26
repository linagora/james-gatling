package org.apache.james.gatling

import java.net.URL

import courier.{Envelope, Mailer, Text}
import javax.mail.internet.InternetAddress
import org.apache.james.gatling.control.{Domain, JamesWebAdministration, User, Username}
import org.slf4j.{Logger, LoggerFactory}
import org.testcontainers.containers.GenericContainer

import scala.concurrent.Await
import scala.concurrent.duration._

object JamesServer {

  private val WAIT_TIMEOUT = 30 seconds
  private val jmapPort = 80
  private val imapPort = 143
  private val smtpPort = 587
  private val webadminPort = 8000
  private val logger: Logger = LoggerFactory.getLogger(JamesServer.getClass)

  class RunningServer(container: GenericContainer[_]) {
    lazy val mappedJmapPort: Integer = container.getMappedPort(jmapPort)
    lazy val mappedWebadminPort: Integer = container.getMappedPort(webadminPort)
    lazy val mappedSmtpPort: Integer = container.getMappedPort(smtpPort)
    lazy val mappedImapPort: Integer = container.getMappedPort(imapPort)

    private lazy val administration = new JamesWebAdministration(new URL(s"http://localhost:$mappedWebadminPort"))

    def addUser(user: User): Unit = Await.result(administration.addUser(user), WAIT_TIMEOUT)

    def addDomain(domain: Domain): Unit = Await.result(administration.addDomain(domain), WAIT_TIMEOUT)

    private def userNameToInternetAddress(username: Username) = {
      new InternetAddress(username.value)
    }

    private val MAIL_SUBJECT = "D'oh!"
    private val MAIL_CONTENT = "Trying is the first step towards failure"
    private val mailerBuilder = Mailer("localhost", container.getMappedPort(587))

    def sendMessage(from: User)(to: Username): Unit = {
      import courier.Defaults._
      val mailer = mailerBuilder.as(from.username.value, from.password.value)()
      Await.result(mailer(Envelope.from(userNameToInternetAddress(from.username))
        .to(userNameToInternetAddress(to))
        .subject(MAIL_SUBJECT)
        .content(Text(MAIL_CONTENT))), 1 second)
    }

    def stop(): Unit = container.stop()

  }

  def start(): RunningServer = {
    val james = new GenericContainer("linagora/james-memory")
    james.addExposedPorts(jmapPort, imapPort, smtpPort, webadminPort)
    james.start()
    new RunningServer(james)
  }

}
