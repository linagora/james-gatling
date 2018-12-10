package org.apache.james.gatling.jmap.scenari.common

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.apache.james.gatling.configuration.Configuration

object HttpSettings {

  val httpProtocol = http
    .baseURL(Configuration.BaseJmapUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json; charset=UTF-8")

}
