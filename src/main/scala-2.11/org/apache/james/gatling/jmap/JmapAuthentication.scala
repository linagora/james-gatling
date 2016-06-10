package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class JmapAuthentication extends Simulation {

  val domain = "domain-jmapauthentication.tld"
  val username = "username@" + domain
  val password = "password"

  val httpProtocol = http
    .baseURL("http://127.0.0.1")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json; charset=UTF-8")

  def obtainContinuationToken(username: String) = exec(
    http("obtainContinuationToken")
      .post("/authentication")
      .body(StringBody(s"""{"username": "$username",
        "clientName": "Mozilla Thunderbird",
        "clientVersion": "42.0",
        "deviceName": "Joe Blogg’s iPhone"}"""))
      .check(status.is(200))
      .check(jsonPath("$.continuationToken").saveAs("continuationToken")))

  def obtainAccessToken(password: String) = exec(
    http("obtainAccessToken")
      .post("/authentication")
      .body(StringBody(s"""{"token": "$${continuationToken}",
        "method": "password",
        "password": "$password"}"""))
      .check(status.is(201))
      .check(jsonPath("$.accessToken").saveAs("accessToken"))
      .check(jsonPath("$.api").saveAs("api"))
      .check(jsonPath("$.eventSource").saveAs("eventSource"))
      .check(jsonPath("$.upload").saveAs("upload"))
      .check(jsonPath("$.download").saveAs("download")))

  def authentication(username: String, password: String) = obtainContinuationToken(username)
    .pause(1 second)
    .exec(obtainAccessToken(password))

  val scn = scenario("JmapAuthentication")
    .exec(authentication(username, password))

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
