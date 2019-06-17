package org.apache.james.gatling.smtp

import akka.actor.{Actor, Props}
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import org.apache.james.gatling.control.UserFeeder

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

  val smtpHandler = context.actorOf(SmtpHandler.props())

  val name = "sendMail"

  def execute(session: Session) {
    smtpHandler ! generateSendMailRequest(session)
  }

  private def generateSendMailRequest(session: Session) = {
    def readSession(variableName: String) = session(variableName).as[String]
    SendMailRequest(session = session,
      host = protocol.host,
      port = protocol.port,
      ssl = protocol.ssl,
      from = readSession(UserFeeder.usernameSessionParam),
      to = readSession(UserFeeder.usernameSessionParam),
      subject = subject,
      body = body,
      credentials = provideCredentialsIfNeeded(readSession)(protocol))
  }

  override def receive: Receive = {
    case session: Session => execute(session)
    case executionReport: ExecutionReport =>
      statsEngine.logResponse(executionReport.session, requestName, executionReport.responseTimings.startTimestamp, executionReport.responseTimings.endTimestamp,
        executionReport.status, None, executionReport.errorMessage)
      next ! executionReport.session
    case msg => logger.error(s"Unexpected message $msg")
  }

  def provideCredentialsIfNeeded(sessionReader: String => String)(protocol: SmtpProtocol): Option[Credentials] = {
    if (protocol.auth) None
    else Some(Credentials(sessionReader(UserFeeder.usernameSessionParam), sessionReader(UserFeeder.passwordSessionParam)))
  }
}
