package org.apache.james.gatling.smtp.scenari.common

import org.apache.james.gatling.configuration.Configuration
import org.apache.james.gatling.smtp.{SmtpActionBuilder, SmtpProtocol, SmtpProtocolBuilder}

object SmtpSettings {

  val smtp: SmtpProtocol = SmtpProtocolBuilder.default.host(Configuration.ServerHostName).build()

  def smtp(requestName: String) = SmtpActionBuilder(requestName, null, null)
}
