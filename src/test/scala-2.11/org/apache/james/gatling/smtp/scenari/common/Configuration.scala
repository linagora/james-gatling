package org.apache.james.gatling.smtp.scenari.common

import org.apache.james.gatling.smtp.{SmtpActionBuilder, SmtpProtocolBuilder}

object Configuration {

  val smtp = SmtpProtocolBuilder.default.build()

  def smtp(requestName: String) = new SmtpActionBuilder(requestName, null, null)
}
