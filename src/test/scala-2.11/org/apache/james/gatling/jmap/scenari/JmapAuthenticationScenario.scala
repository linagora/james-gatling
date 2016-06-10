package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.apache.james.gatling.jmap.JmapAuthentication.authentication

class JmapAuthenticationScenario extends Simulation {

  val domain = "domain-jmapauthentication.tld"
  val username = "username@" + domain
  val password = "password"

  val httpProtocol = http
    .baseURL("http://127.0.0.1")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json; charset=UTF-8")

  val scn = scenario("JmapAuthentication")
    .exec(authentication(username, password))

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

}
