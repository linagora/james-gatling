package org.apache.james.gatling.control

import java.net.URL

import org.apache.james.gatling.utils.RandomStringGenerator
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Domain(value: String)
case class Username(value: String)
case class Password(value: String)
case class User(username: Username, password: Password)

object User {
  def random(domain: Domain) = {
    val localAddressPart = RandomStringGenerator.randomString
    User(Username(s"$localAddressPart@${domain.value}"),
      Password.random)
  }
}

object Domain {
  def random = Domain(RandomStringGenerator.randomString)
}

object Password {
  def random = Password(RandomStringGenerator.randomString)
}

class JamesWebAdministration(val baseUrl: URL) {
  val wsClient = NingWSClient()

  def addDomain(domain: Domain): Future[Domain] = wsClient.url(s"$baseUrl/domains/${domain.value}")
    .put("")
    .map(response => domain)

  def addUser(user: User): Future[User] =
    wsClient.url(s"$baseUrl/users/${user.username.value}")
      .put(s"""{"password":"${user.password.value}"}""")
      .map(response => user)

  def getMailboxesUrl(username: Username): URL =
    new URL(s"$baseUrl/users/${username.value}/mailboxes")

  def createInbox(username: Username) = {
    val mailboxesUrl = getMailboxesUrl(username)
    wsClient.url(s"$mailboxesUrl/INBOX").put("")
  }

  def createOutbox(username: Username) = {
    val mailboxesUrl = getMailboxesUrl(username)
    wsClient.url(s"$mailboxesUrl/Outbox").put("")
  }

  def createSentBox(username: Username) = {
    val mailboxesUrl = getMailboxesUrl(username)
    wsClient.url(s"$mailboxesUrl/Sent").put("")
  }

}
