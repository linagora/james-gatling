package org.apache.james.gatling.control

import play.api.libs.ws.ning.NingWSClient

object JamesWebAdministration {

  val wsClient = NingWSClient()

  val baseUrl: String = "http://172.17.0.4:8000"

  def addDomain(domain: String) = wsClient.url(s"$baseUrl/domains/$domain")
    .put("")

  def addUser(username: String, password: String) = wsClient.url(s"$baseUrl/users/$username")
    .put(s"""{"password":"$password"}""")


}
