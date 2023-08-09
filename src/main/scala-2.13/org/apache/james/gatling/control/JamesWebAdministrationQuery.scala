package org.apache.james.gatling.control

import java.net.URL

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class JamesWebAdministrationQuery(val baseUrl: URL) {

  def getMailQueueMails(name: String) =
    exec(
      http(s"getMailQueues:${name}")
        .get(s"$baseUrl/mailQueues/${name}/mails")
        .check(status.is(200)))
}
