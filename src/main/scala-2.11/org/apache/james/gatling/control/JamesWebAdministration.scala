package org.apache.james.gatling.control

import java.net.URL

import play.api.libs.ws.WSResponse
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Future

case class Domain(value: String)
case class Username(value: String)
case class Password(value: String)
case class User(username: Username, password: Password)

object JamesWebAdministration {

  val wsClient = NingWSClient()

  val baseUrl = new URL("http://172.17.0.4:8000")

  def addDomain(domain: Domain): Future[WSResponse] = wsClient.url(s"$baseUrl/domains/${domain.value}")
    .put("")

  def addUser(user: User): Future[WSResponse] =
    wsClient.url(s"$baseUrl/users/${user.username.value}")
      .put(s"""{"password":"${user.password.value}"}""")

  def createInbox(username: Username) = wsClient.url(s"$baseUrl/users/${username.value}/mailboxes/INBOX").put("")
  def createOutbox(username: Username) = wsClient.url(s"$baseUrl/users/${username.value}/mailboxes/outbox").put("")
  def createSentBox(username: Username) = wsClient.url(s"$baseUrl/users/${username.value}/mailboxes/sent").put("")

}
