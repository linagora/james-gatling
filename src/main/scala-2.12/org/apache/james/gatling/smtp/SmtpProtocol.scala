package org.apache.james.gatling.smtp

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolComponents, ProtocolKey}
import io.gatling.core.session.Session

object SmtpProtocol {

  val SmtpProtocolKey = new ProtocolKey[SmtpProtocol, SmtpComponents] {

    override def protocolClass: Class[io.gatling.core.protocol.Protocol] = classOf[SmtpProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): SmtpProtocol = default

    override def newComponents(coreComponents: CoreComponents): SmtpProtocol => SmtpComponents = {
      smtpProtocol => SmtpComponents(smtpProtocol)
    }
  }

  case class SmtpComponents(protocol: SmtpProtocol) extends ProtocolComponents {
    override def onStart: Session => Session = s => s

    override def onExit: Session => Unit = s => ()
  }

  val DEFAULT_PORT = 25
  val DEFAULT_PORT_SSL = 465

  val default = new SmtpProtocol("localhost", false, DEFAULT_PORT, false)

  def defaultPort(ssl: Boolean) = if (ssl) SmtpProtocol.DEFAULT_PORT_SSL else SmtpProtocol.DEFAULT_PORT

  val smtp = SmtpProtocol.default

  def smtp(requestName: String) = new SmtpActionBuilder(requestName, null, null)

}

case class SmtpProtocol(
                         host: String,
                         ssl: Boolean,
                         port: Int,
                         auth: Boolean) extends Protocol {
}

