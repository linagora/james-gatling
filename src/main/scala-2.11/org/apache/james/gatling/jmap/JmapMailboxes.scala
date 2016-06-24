package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import io.gatling.http.request.builder.HttpRequestBuilder

object JmapMailboxes {

  def getMailboxes(dataExtractors: (HttpRequestBuilder => HttpRequestBuilder)*) =
    if (dataExtractors.nonEmpty) {
      dataExtractors.map(dataExtractor => new PipelineElement(dataExtractor))
        .reduce(_ combineWith _)
        .applyPipeline(allMailboxesRequest)
    } else {
      allMailboxesRequest
    }

  /* available data extractors */

  def extractSentId(httpRequestBuilder: HttpRequestBuilder) = extractMailboxWithRole("sent", "sentMailboxId")(httpRequestBuilder)
  def extractInboxId(httpRequestBuilder: HttpRequestBuilder) = extractMailboxWithRole("inbox", "inboxMailboxId")(httpRequestBuilder)
  def extractOutboxId(httpRequestBuilder: HttpRequestBuilder) = extractMailboxWithRole("outbox", "outboxMailboxId")(httpRequestBuilder)

  def extractMailboxesIds(httpRequestBuilder: HttpRequestBuilder) = httpRequestBuilder
    .check(jsonPath("$[0][1].list[*].id").saveAs("mailboxIds"))

  def extractMailboxWithRole(role: String, sessionVariableName: String)(httpRequestBuilder: HttpRequestBuilder) =
    extractMailboxWithAttribute("role")(role, sessionVariableName)(httpRequestBuilder)
  def extractMailboxWithName(name: String, sessionVariableName: String)(httpRequestBuilder: HttpRequestBuilder) =
    extractMailboxWithAttribute("name")(name, sessionVariableName)(httpRequestBuilder)

  def extractMailboxWithAttribute(attributeKey: String)(value: String, sessionVariableName: String)(httpRequestBuilder: HttpRequestBuilder): HttpRequestBuilder = {
    val path: String = s"$$[0][1].list[?(@.$attributeKey == '$value')].id"
    httpRequestBuilder.check(jsonPath(path).saveAs(sessionVariableName))
  }

  /* The request to process */

  def allMailboxesRequest = JmapAuthentication.authenticatedQuery("getMailboxes", "/jmap")
    .body(StringBody("""[["getMailboxes", {}, "#0"]]"""))
    .check(status.is(200))
    .check(jsonPath("$.error").notExists)

  class PipelineElement (val dataExtractor: HttpRequestBuilder => HttpRequestBuilder, val next: Option[PipelineElement]) {

    def this(dataExtractor: HttpRequestBuilder => HttpRequestBuilder) = this(dataExtractor, None)

    def combineWith(next: PipelineElement) = new PipelineElement(dataExtractor, Some(next))

    def applyPipeline(httpRequestBuilder: HttpRequestBuilder): HttpRequestBuilder = dataExtractor.apply(next
      .map(pipeLineElement => pipeLineElement.applyPipeline(httpRequestBuilder))
      .getOrElse(httpRequestBuilder))
  }
}
