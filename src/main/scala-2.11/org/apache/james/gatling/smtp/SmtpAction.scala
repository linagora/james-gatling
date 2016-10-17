package org.apache.james.gatling.smtp

import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import org.apache.commons.mail.SimpleEmail
import org.apache.james.gatling.control.UserFeeder

import scala.util.{Failure, Success, Try}

class SmtpAction(
                  requestName: String,
                  subject: String,
                  body: String,
                  val statsEngine: StatsEngine,
                  val next: Action,
                  protocol: SmtpProtocol) extends ChainableAction {

  val name = "sendMail"

  def execute(session: Session) {
    val email: SimpleEmail = generateEmail(session)
    val reqStart = System.currentTimeMillis()
    val executionReport = sendMail(email)
    val reqEnd = System.currentTimeMillis()

    statsEngine.logResponse(session, requestName, ResponseTimings(reqStart, reqEnd), executionReport.status, None, executionReport.errorMessage)

    next ! session
  }

  private def generateEmail(session: Session): SimpleEmail = {
    def sessionVariable(variableName: String): String = {
      session.apply(variableName).as[String]
    }

    val email = new SimpleEmail()
    val userEmail = sessionVariable(UserFeeder.UsernameSessionParam)
    if (protocol.auth) {
      email.setAuthentication(sessionVariable(UserFeeder.UsernameSessionParam), sessionVariable(UserFeeder.PasswordSessionParam))
    }
    email.setHostName(protocol.host)
    email.setSSLOnConnect(protocol.ssl)
    email.setSmtpPort(protocol.port)
    email.setSubject(subject + " [" + Math.random() + "]")
    email.setMsg(body)
    email.setFrom(userEmail)
    email.addTo(userEmail)
    email
  }

  private def sendMail(email: SimpleEmail): ExecutionReport = {
    Try(email.send()) match {
      case Success(v) => GoodExecutionReport()
      case Failure(e) =>
        logger.error("Exception caught while sending mail", e)
        BadExecutionReport(e.getMessage)
    }
  }
}

case class ExecutionReport(errorMessage: Option[String], status: Status)
case class GoodExecutionReport() extends ExecutionReport(None, OK)
case class BadExecutionReport(message: String) extends ExecutionReport(Some(message), KO)