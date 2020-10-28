package org.apache.james.gatling.jmap.draft

object JmapHttp {
  val CONTENT_TYPE_JSON_KEY: String = "Content-Type"
  val CONTENT_TYPE_JSON_VALUE: String = "application/json; charset=UTF-8"

  val ACCEPT_JSON_KEY: String = "Accept"
  val ACCEPT_JSON_VALUE: String = "application/json"

  val HEADERS_JSON = Map(CONTENT_TYPE_JSON_KEY -> CONTENT_TYPE_JSON_VALUE, ACCEPT_JSON_KEY -> ACCEPT_JSON_VALUE)
  
}
