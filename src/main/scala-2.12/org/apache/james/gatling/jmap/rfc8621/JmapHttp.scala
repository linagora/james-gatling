package org.apache.james.gatling.jmap.rfc8621

object JmapHttp {
  val CONTENT_TYPE_JSON_KEY: String = "Content-Type"
  val CONTENT_TYPE_JSON_VALUE: String = "application/json; charset=UTF-8"

  val ACCEPT_JSON_KEY: String = "Accept"
  val ACCEPT_JSON_VALUE: String = "application/json; jmapVersion=rfc-8621"

  val HEADERS_JSON = Map(CONTENT_TYPE_JSON_KEY -> CONTENT_TYPE_JSON_VALUE, ACCEPT_JSON_KEY -> ACCEPT_JSON_VALUE)
  
}
