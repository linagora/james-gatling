package org.apache.james.gatling.control

import java.net.URL

import org.apache.james.gatling.jmap.JmapHttp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.json._
import play.api.libs.ws.DefaultBodyWritables._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JamesJmap(val baseUrl: URL) {
  // Create Akka system for thread and streaming management
  implicit val system = ActorSystem()
  system.registerOnTermination {
    System.exit(0)
  }
  implicit val materializer = ActorMaterializer()
  val wsClient = StandaloneAhcWSClient()
  val authenticationUrl = s"$baseUrl/authentication"

  def authenticateUser(user: User): Future[AuthenticatedUser] = {
    wsClient.url(authenticationUrl)
      .withHttpHeaders(JmapHttp.HEADERS_JSON.toSeq: _*)
      .post(s"""{"username": "${user.username.value}",
          "clientName": "OpenPaaS",
          "clientVersion": "42.0",
          "deviceName": "Marvin's Firefox OS"}""")
      .map(response => (Json.parse(response.body) \ "continuationToken").validate[String].get)
      .flatMap(continuationToken => wsClient.url(authenticationUrl)
        .withHttpHeaders(JmapHttp.HEADERS_JSON.toSeq: _*)
        .post(s"""{"token": "${continuationToken}",
            "method": "password",
            "password": "${user.password.value}"}"""))
      .map(response => (Json.parse(response.body) \ "accessToken").validate[String].get)
      .map(jwtAccessToken => user.authenticate(JwtAccessToken(jwtAccessToken)))
  }
}
