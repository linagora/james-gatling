package org.apache.james.gatling.smtp

import akka.actor.Props
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef.Status
import io.gatling.core.akka.BaseActor
import io.gatling.core.session.Session
import io.gatling.core.stats.message.ResponseTimings
import org.apache.commons.mail.SimpleEmail

import scala.util.{Failure, Success, Try}

object SmtpHandler {
  def props(): Props = Props(new SmtpHandler)
}

class SmtpHandler extends BaseActor {
  override def receive: Receive = {
    case sendMailRequest: SendMailRequest =>
      sender ! send(sendMailRequest)
      context.stop(self)
    case msg =>
      logger.error(s"received unexpected message $msg")
  }

  def send(sendMailRequest: SendMailRequest): ExecutionReport = doSend(generateEmail(sendMailRequest), sendMailRequest.session)

  private def generateEmail(sendMailRequest: SendMailRequest): SimpleEmail = {
    val email = new SimpleEmail()
    sendMailRequest.credentials.fold {} {value => email.setAuthentication(value.login, value.password)}
    email.setHostName(sendMailRequest.host)
    email.setSSLOnConnect(sendMailRequest.ssl)
    email.setSmtpPort(sendMailRequest.port)
    email.setSubject(sendMailRequest.subject + " [" + Math.random() + "]")
    email.setMsg(sendMailRequest.body)
    email.setFrom(sendMailRequest.from)
    email.addTo(sendMailRequest.to)
    email
  }

  private def doSend(email: SimpleEmail, session: Session): ExecutionReport = {
    val requestStart = System.currentTimeMillis()
    Try(email.send()) match {
      case Success(v) => GoodExecutionReport(computeResponseTimings(requestStart), session)
      case Failure(e) =>
        logger.error("Exception caught while sending mail", e)
        BadExecutionReport(e.getMessage, computeResponseTimings(requestStart), session)
    }
  }

  private def computeResponseTimings(reqStart: Long) = {
    val requestEnd = System.currentTimeMillis()
    ResponseTimings(reqStart, requestEnd)
  }
}

case class SendMailRequest(session: Session,
                           host: String,
                           port: Int,
                           ssl: Boolean,
                           from: String,
                           to: String,
                           subject: String,
                           body: String,
                           credentials: Option[Credentials])

case class Credentials(login: String, password: String)

abstract class ExecutionReport(_errorMessage: Option[String], _status: Status, _responseTimings: ResponseTimings, _session: Session) {
  def errorMessage = _errorMessage
  def status = _status
  def responseTimings = _responseTimings
  def session = _session
}

case class GoodExecutionReport(_responseTimings: ResponseTimings, _session: Session)
  extends ExecutionReport(None, OK, _responseTimings, _session)

case class BadExecutionReport(message: String, _responseTimings: ResponseTimings, _session: Session)
  extends ExecutionReport(Some(message), KO, _responseTimings, _session)