package org.apache.james.gatling.smtp

import akka.actor.ActorSystem
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.Protocol
import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.protocol.ProtocolKey
import io.gatling.core.session.Session

object SmtpProtocol {
  val SmtpProtocolKey = new ProtocolKey {
    override type Protocol = SmtpProtocol
    override type Components = SmtpComponents

    override def protocolClass: Class[io.gatling.core.protocol.Protocol] = classOf[SmtpProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    override def defaultValue(configuration: GatlingConfiguration): SmtpProtocol = default

    override def newComponents(system: ActorSystem, coreComponents: CoreComponents): SmtpProtocol => SmtpComponents = {
     smtpProtocol => SmtpComponents(smtpProtocol)
    }
  }

case class SmtpComponents(protocol: SmtpProtocol) extends ProtocolComponents {
  override def onStart: Option[(Session) => Session] = None

  override def onExit: Option[(Session) => Unit] = None
}


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
