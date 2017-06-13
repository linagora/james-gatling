package org.apache.james.gatling.smtp

import akka.actor.{Actor, Props}
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import org.apache.james.gatling.control.UserFeeder

import scala.util.Failure

object SmtpAction {
  def props(requestName: String, subject: String, body: String, statsEngine: StatsEngine, next: Action, protocol: SmtpProtocol) =
    Props(new SmtpAction(requestName, subject, body, statsEngine, next, protocol))
}

class SmtpAction(requestName: String,
                  subject: String,
                  body: String,
                  val statsEngine: StatsEngine,
                  val next: Action,
                  protocol: SmtpProtocol) extends ChainableAction with Actor {

  val name = "sendMail"

  def execute(session: Session) {
    def readSession(variableName: String): String = session.apply(variableName).as[String]
    val request = SendMailRequest(session = session,
      host = protocol.host,
      port = protocol.port,
      ssl = protocol.ssl,
      from = readSession(UserFeeder.UsernameSessionParam),
      to = readSession(UserFeeder.UsernameSessionParam),
      subject = subject,
      body = body,
      credentials = readCredentials(readSession)(protocol))

    context.actorOf(SmtpHandler.props()) ! request

    next ! session
  }

  override def receive: Receive = {
    case session: Session => execute(session)
    case executionReport: ExecutionReport =>
      statsEngine.logResponse(executionReport.session, requestName, executionReport.responseTimings, executionReport.status, None, executionReport.errorMessage)
    case Failure(e) =>
      logger.error("Exception caught while sending mail", e)
  }

  def readCredentials(sessionReader: String => String)(protocol: SmtpProtocol): Option[Credentials] = {
    if (protocol.auth) None
    else Some(Credentials(sessionReader(UserFeeder.UsernameSessionParam), sessionReader(UserFeeder.PasswordSessionParam)))
  }
}
