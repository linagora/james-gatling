package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object HttpSettings {

  val httpProtocol = http
    .baseURL("http://127.0.0.1")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json; charset=UTF-8")

}
