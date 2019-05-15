package org.apache.james.gatling.smtp

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolComponents, ProtocolKey}
import io.gatling.core.session.Session

import scala.util.Properties

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

  val PORT = Properties.envOrElse("SMTP_PORT", "25").toInt
  val PORT_SSL = Properties.envOrElse("SMTP_SSL_PORT", "465").toInt
  val HOSTNAME = Properties.envOrElse("TARGET_HOSTNAME", "localhost")

  val default = new SmtpProtocol(HOSTNAME, false, PORT, false)

  def defaultPort(ssl: Boolean) = if (ssl) SmtpProtocol.PORT_SSL else SmtpProtocol.PORT

  val smtp = SmtpProtocol.default

  def smtp(requestName: String) = new SmtpActionBuilder(requestName, null, null)

}

case class SmtpProtocol(
                         host: String,
                         ssl: Boolean,
                         port: Int,
                         auth: Boolean) extends Protocol {
}

