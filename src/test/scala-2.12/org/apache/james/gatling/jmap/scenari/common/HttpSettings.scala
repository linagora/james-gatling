package org.apache.james.gatling.jmap.scenari.common

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object HttpSettings {

  val httpProtocol = http
    .baseUrl(Configuration.BaseJmapUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json; charset=UTF-8")

}
