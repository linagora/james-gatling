package org.apache.james.gatling

import java.net.URL
import java.nio.charset.StandardCharsets

import javax.mail.internet.InternetAddress
import org.apache.james.gatling.control.{Domain, JamesWebAdministration, User, Username}
import org.apache.james.gatling.jmap.MailboxName
import org.slf4j.{Logger, LoggerFactory}
import org.testcontainers.containers.GenericContainer
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.{Await, Future}
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
    lazy val mappedWebadmin = new JamesWebAdministration(new URL(s"http://localhost:$mappedWebadminPort"))

    def addUser(user: User): Unit = Await.result(mappedWebadmin.addUser(user), WAIT_TIMEOUT)

    def addDomain(domain: Domain): Unit = Await.result(mappedWebadmin.addDomain(domain), WAIT_TIMEOUT)

    private def userNameToInternetAddress(username: Username) = {
      new InternetAddress(username.value)
    }

    private val MAIL_AUTHOR = Username("moe@simpson.cartoon")
    private val MAIL_SUBJECT = "D'oh!"
    private val MAIL_CONTENT = "Trying is the first step towards failure"
    private val INBOX = MailboxName("INBOX")

    def sendMessage(from: Username)(to: User): Unit =
      putMessage(from)(to)(INBOX)

    def createMailbox(username: Username)(mailboxName: MailboxName): Future[StandaloneWSRequest#Response] =
      mappedWebadmin.createMailbox(username, mailboxName)


    def sendMessage(user: User): MailboxName => Unit =
      putMessage(MAIL_AUTHOR)(user)

    private def putMessage(from: Username)(to: User)(mailboxName: MailboxName): Unit = {
      import javax.mail.Session
      import javax.mail.internet.MimeMessage
      val session = Session.getDefaultInstance(System.getProperties, null)
      val store = session.getStore("imap")
      store.connect(s"localhost", mappedImapPort, to.username.value, to.password.value)

      // Get default folder
      val folder = store.getFolder(mailboxName.name)
      val message = makeMessage(from.value, to.username.value, MAIL_SUBJECT, MAIL_CONTENT)
      folder.appendMessages(Array(new MimeMessage(session, message)))
    }

    private def makeMessage(author: String, recipient: String, subject: String, content: String) = {
      import java.io.ByteArrayInputStream
      val body =
        s"""From: $author
           |To: $recipient
           |Subject: $subject
           |
           |$content""".stripMargin
      new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))
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
