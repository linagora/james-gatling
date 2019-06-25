package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.core.check.extractor.jsonpath.{JsonPathCheckBuilder, JsonPathOfType}

object JmapChecks {

  private val hasErrorPath: JsonPathCheckBuilder[String] with JsonPathOfType = jsonPath("$[?(@[0] == 'error')]")

  val noError = hasErrorPath.notExists
  val hasError = hasErrorPath.exists

  def created() = jsonPath(s"$$[0][1].created['$${${JmapMessages.messageIdSessionParam}}'].id").exists

}
