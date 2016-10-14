package org.apache.james.gatling.smtp

object PreDef {

  def smtp = SmtpProtocolBuilder.default
  implicit def smtpProtocolBuilder2SmtpProtocol(builder: SmtpProtocolBuilder): SmtpProtocol = builder.build()

  def smtp(requestName: String) = new SmtpActionBuilder(requestName, null, null)

}
