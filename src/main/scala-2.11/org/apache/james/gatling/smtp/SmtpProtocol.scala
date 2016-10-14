package org.apache.james.gatling.smtp

import io.gatling.core.protocol.Protocol

object SmtpProtocol {

  val DEFAULT_PORT = 25
  val DEFAULT_PORT_SSL = 465

  val default = new SmtpProtocol("localhost", false, DEFAULT_PORT, false)

  def defaultPort(ssl: Boolean) = if (ssl) SmtpProtocol.DEFAULT_PORT_SSL else SmtpProtocol.DEFAULT_PORT

}

case class SmtpProtocol(
                         host: String,
                         ssl: Boolean,
                         port: Integer,
                         auth: Boolean) extends Protocol {

}
