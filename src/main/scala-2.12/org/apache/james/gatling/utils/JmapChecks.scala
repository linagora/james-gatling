package org.apache.james.gatling.utils

import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.http.Predef._
import org.apache.james.gatling.jmap.MessageId

object JmapChecks {

  val noError = jsonPath("$.error").notExists

  def created(messageId: MessageId) = jsonPath(s"$$[0][1].created['${messageId.id}'].id").exists

}
