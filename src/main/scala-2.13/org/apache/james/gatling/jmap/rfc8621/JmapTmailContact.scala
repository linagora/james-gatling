package org.apache.james.gatling.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object JmapTmailContact {

  def getAutocomplete(accountId: String = "accountId",
                      typeaheadKeyword: String = "typeaheadKeyword"): HttpRequestBuilder =
    JmapHttp.apiCall("TmailContactAutocomplete")
      .body(StringBody(
        s"""{
           |  "using": ["urn:ietf:params:jmap:core", "com:linagora:params:jmap:contact:autocomplete"],
           |  "methodCalls": [[
           |    "TMailContact/autocomplete",
           |    {
           |      "accountId": "#{$accountId}",
           |      "filter": {"text":"#{$typeaheadKeyword}"}
           |    },
           |    "c1"]]
           |}""".stripMargin))
}
