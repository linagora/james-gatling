package org.apache.james.gatling.smtp

import javax.mail.internet.InternetAddress

import akka.actor.{ActorRef, Props}
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef.Status
import io.gatling.core.akka.BaseActor
import io.gatling.core.session.Session
import io.gatling.core.stats.message.ResponseTimings

import scala.concurrent.ExecutionContext.global

object SmtpHandler {
  def props(): Props = Props(new SmtpHandler)
}

class SmtpHandler extends BaseActor {
  override def receive: Receive = {
    case sendMailRequest: SendMailRequest => sendMail(sendMailRequest, sender)
    case msg => logger.error(s"received unexpected message $msg")
  }

  def waitCallback(sender: ActorRef): Receive = {
    case executionReport: ExecutionReport =>
      sender ! executionReport
      context.stop(self)
    case msg => logger.error(s"received unexpected message while expecting response $msg")
  }

  def sendMail(sendMailRequest: SendMailRequest, requestOrigin: ActorRef) = {
    import courier._
    val baseMailer = Mailer(sendMailRequest.host, sendMailRequest.port)
      .startTtls(sendMailRequest.ssl)

    val mailer = sendMailRequest.credentials
      .map(value => baseMailer.auth(true)
        .as(value.login, value.password))
      .getOrElse(baseMailer.auth(false))()

    val requestStart = System.currentTimeMillis()

    val future = mailer(Envelope.from(new InternetAddress(sendMailRequest.from))
      .to(new InternetAddress(sendMailRequest.to))
      .subject(sendMailRequest.subject)
      .content(Text(sendMailRequest.body)))

    future.onSuccess {case _ =>
      requestOrigin ! GoodExecutionReport(computeResponseTimings(requestStart), sendMailRequest.session)
    }

    future.onFailure {case e =>
      logger.error("Exception caught while sending mail", e)
      requestOrigin ! BadExecutionReport(e.getMessage, computeResponseTimings(requestStart), sendMailRequest.session)
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