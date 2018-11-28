package org.apache.james.gatling.smtp

object PreDef {

  implicit def smtpProtocolBuilder2SmtpProtocol(builder: SmtpProtocolBuilder): SmtpProtocol = builder.build()

}
