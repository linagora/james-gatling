package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import org.apache.james.gatling.jmap.HttpSettings
import org.apache.james.gatling.jmap.JmapAuthentication.authentication

class JmapAuthenticationScenario extends Simulation {

  val domain = "domain-jmapauthentication.tld"
  val username = "username@" + domain
  val password = "password"

  val scn = scenario("JmapAuthentication")
    .exec(authentication(username, password))

  setUp(scn.inject(atOnceUsers(1))).protocols(HttpSettings.httpProtocol)

}
