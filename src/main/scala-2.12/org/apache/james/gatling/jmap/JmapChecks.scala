package org.apache.james.gatling.jmap

import io.gatling.core.Predef._

object JmapChecks {

  val noError = jsonPath("$.error").notExists

  def created() = jsonPath("$[0][1].created['${messageId}'].id").exists

}
