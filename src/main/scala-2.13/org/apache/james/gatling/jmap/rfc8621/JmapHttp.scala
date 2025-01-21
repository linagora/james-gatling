package org.apache.james.gatling.jmap.rfc8621

import com.fasterxml.jackson.databind.JsonNode
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder.MultipleFind
import io.gatling.core.check.jsonpath.{JsonPathCheckType, JsonPathOfType}
import io.gatling.http.HeaderNames
import io.gatling.http.Predef.{http, status}
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.utils.RandomStringGenerator


object JmapHttp {
  val CONTENT_TYPE_JSON_KEY: String = HeaderNames.ContentType.toString
  val CONTENT_TYPE_JSON_VALUE: String = "application/json; charset=UTF-8"
  val CONTENT_TYPE_TEXT_PLAIN: String = "text/plain"

  val ACCEPT_JSON_KEY: String = HeaderNames.Accept.toString
  val ACCEPT_JSON_VALUE: String = "application/json; jmapVersion=rfc-8621"

  val HEADERS_JSON = Map(CONTENT_TYPE_JSON_KEY -> CONTENT_TYPE_JSON_VALUE, ACCEPT_JSON_KEY -> ACCEPT_JSON_VALUE)

  def apiCall(callName: String): HttpRequestBuilder = http(callName)
    .post("/jmap")
    .headers(JmapHttp.HEADERS_JSON)
    .basicAuth("#{username}", "#{password}")

  def download(callName: String = "Download", accountId: String = "#{accountId}", blobId: String = "#{blobId}"): HttpRequestBuilder =
    http(callName)
      .get(s"/download/$accountId/$blobId")
      .headers(JmapHttp.HEADERS_JSON)
      .basicAuth("#{username}", "#{password}")

  def upload(callName: String = "Upload", accountId: String = "#{accountId}", body: String = RandomStringGenerator.randomAlphaString(1000)): HttpRequestBuilder =
    http(callName)
      .post(s"/upload/$accountId")
      .body(StringBody(body))
      .headers(Map(CONTENT_TYPE_JSON_KEY -> CONTENT_TYPE_TEXT_PLAIN, ACCEPT_JSON_KEY -> ACCEPT_JSON_VALUE))
      .basicAuth("#{username}", "#{password}")

  private val hasErrorPath: MultipleFind[JsonPathCheckType, JsonNode, String] with JsonPathOfType = jsonPath("$[?(@[0] == 'error')]")
  val noError = hasErrorPath.notExists
  val hasError = hasErrorPath.exists
  val statusOk = status.is(200)
}
