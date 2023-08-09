package org.apache.james.gatling.jmap.draft

import com.fasterxml.jackson.databind.JsonNode
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder.MultipleFind
import io.gatling.core.check.jsonpath.{JsonPathCheckType, JsonPathOfType}

object JmapChecks {

  private val hasErrorPath: MultipleFind[JsonPathCheckType, JsonNode, String] with JsonPathOfType = jsonPath("$[?(@[0] == 'error')]")

  val noError = hasErrorPath.notExists
  val hasError = hasErrorPath.exists

  def created() = jsonPath(s"$$[0][1].created['$${${JmapMessages.messageIdSessionParam}}'].id").exists

}
