package org.apache.james.gatling.smtp.scenari.common

import org.apache.james.gatling.smtp.{SmtpActionBuilder, SmtpProtocol}

object Configuration {

  val smtp = SmtpProtocol.default

  def smtp(requestName: String) = new SmtpActionBuilder(requestName, null, null)
}
