package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef.http

object HttpSettings {

  def httpProtocol = http
    .baseUrl(Configuration.BaseJmapUrl.toString)
    .wsBaseUrl(Configuration.BaseWsUrl)

}
