package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef.http
import org.apache.james.gatling.jmap.JmapHttp

object HttpSettings {

  def httpProtocol = http
    .baseUrl(Configuration.BaseJmapUrl)
    .acceptHeader(JmapHttp.ACCEPT_JSON_VALUE)
    .contentTypeHeader(JmapHttp.CONTENT_TYPE_JSON_VALUE)

}
