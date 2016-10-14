package org.apache.james.gatling.smtp

import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import org.apache.commons.mail.SimpleEmail
import org.apache.james.gatling.control.UserFeeder

class SmtpAction(
                  requestName: String,
                  subject: String,
                  body: String,
                  val statsEngine: StatsEngine,
                  val next: Action,
                  protocol: SmtpProtocol) extends ChainableAction {

  val name = "sendMail"

  def execute(session: Session) {
    def sessionVariable(variableName: String): String = {
      session.apply(variableName).as[String]
    }

    val reqStart = System.currentTimeMillis()
    val email = new SimpleEmail()
    var errorMessage: Option[String] = None
    var status: Status = OK
    val userEmail = sessionVariable(UserFeeder.UsernameSessionParam)

    email.setHostName(protocol.host)
    email.setSSLOnConnect(protocol.ssl)
    email.setSmtpPort(protocol.port)

    if (protocol.auth) {
      email.setAuthentication(sessionVariable(UserFeeder.UsernameSessionParam), sessionVariable(UserFeeder.PasswordSessionParam))
    }

    email.setSubject(subject + " [" + Math.round(Math.random() * reqStart) + "]")
    email.setMsg(body)
    email.setFrom(userEmail)
    email.addTo(userEmail)

    try {
      email.send()
    } catch {
      case e: Exception => {
        logger.error("Exception caught while sending mail", e)

        errorMessage = Some(e.toString)
        status = KO
      }
    }

    val reqEnd = System.currentTimeMillis()

    statsEngine.logResponse(session, requestName, ResponseTimings(reqStart, reqEnd), status, None, errorMessage)

    next ! session
  }

}