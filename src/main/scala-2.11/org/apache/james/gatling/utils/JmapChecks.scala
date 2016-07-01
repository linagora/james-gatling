package org.apache.james.gatling.utils

import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.http.Predef._

object JmapChecks {

  val noError = jsonPath("$.error").notExists

}
