package org.apache.james.gatling.smtp

object SmtpProtocolBuilder {

  val default = new SmtpProtocolBuilder(SmtpProtocol.default)

}

case class SmtpProtocolBuilder(protocol: SmtpProtocol) {

  def host(host: String) = copy(protocol.copy(host = host))
  def ssl(ssl: Boolean) = copy(protocol.copy(ssl = ssl))
  def port(port: Int) = copy(protocol.copy(port = port))
  def port(port: String) = if (port != null) copy(protocol.copy(port = Integer.valueOf(port))) else this
  def auth(auth: Boolean) = copy(protocol.copy(auth = auth))

  def build() = {
    if (protocol.port == null) {
      port(SmtpProtocol.defaultPort(protocol.ssl)).protocol
    }

    protocol
  }

}