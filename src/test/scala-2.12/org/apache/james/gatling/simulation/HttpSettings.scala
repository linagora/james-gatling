package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef.http
import org.apache.james.gatling.jmap.draft.JmapHttp

object HttpSettings {

  def httpProtocol = http
    .baseUrl(Configuration.BaseJmapUrl.toString)
    .acceptHeader(JmapHttp.ACCEPT_JSON_VALUE)
    .contentTypeHeader(JmapHttp.CONTENT_TYPE_JSON_VALUE)
    .wsBaseUrl(Configuration.BaseJmapUrl.toString)

}
