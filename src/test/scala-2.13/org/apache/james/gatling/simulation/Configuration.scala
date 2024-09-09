package org.apache.james.gatling.simulation

import java.net.URI

import scala.concurrent.duration._
import scala.util.Properties

object Configuration {

  val ServerHostName = Properties.envOrElse("TARGET_HOSTNAME", "localhost")
  val JmapServerHostName = Properties.envOrElse("JMAP_TARGET_HOSTNAME", ServerHostName)
  val ImapServerHostName = Properties.envOrElse("IMAP_SERVER_HOSTNAME", ServerHostName)
  val WebadminServerHostName = Properties.envOrElse("WEBADMIN_SERVER_HOSTNAME", ServerHostName)

  val JMAP_PORT = Properties.envOrElse("JMAP_PORT", "1080").toInt
  val JMAP_PROTOCOL = Properties.envOrElse("JMAP_PROTOCOL", "http")
  val WS_PROTOCOL = Properties.envOrElse("WS_PROTOCOL", "ws")
  val WS_PORT = Properties.envOrElse("WS_PORT", String.valueOf(JMAP_PORT)).toInt
  val BaseJmapUrl = new URI(s"$JMAP_PROTOCOL://$JmapServerHostName:$JMAP_PORT").toURL
  val BaseWsUrl = s"$WS_PROTOCOL://$JmapServerHostName:$WS_PORT"

  val WEBADMIN_PORT = Properties.envOrElse("WEBADMIN_PORT", "8000").toInt
  val WEBADMIN_PROTOCOL = Properties.envOrElse("WEBADMIN_PROTOCOL", "http")
  val BaseJamesWebAdministrationUrl = new URI(s"$WEBADMIN_PROTOCOL://$WebadminServerHostName:$WEBADMIN_PORT").toURL

  val DURATION_PROPERTY = Properties.envOrNone("DURATION") match {
    case Some(duration) => Some(duration.toInt minutes)
    case _ => None
  }

  val SCENARIO_DURATION_PROPERTY = Properties.envOrNone("SCENARIO_DURATION") match {
    case Some(duration) => Some(duration.toInt minutes)
    case _ => DURATION_PROPERTY
  }

  val INJECTION_DURATION_PROPERTY = Properties.envOrNone("INJECTION_DURATION") match {
    case Some(duration) => Some(duration.toInt minutes)
    case _ => DURATION_PROPERTY
  }

  val MAX_DURATION_PROPERTY = Properties.envOrNone("MAX_DURATION") match {
    case Some(duration) => Some(duration.toInt minutes)
    case _ => None
  }

  val ScenarioDuration = SCENARIO_DURATION_PROPERTY.getOrElse(1 hour)
  val InjectionDuration = INJECTION_DURATION_PROPERTY.getOrElse(1 hour)
  val MaxDuration = MAX_DURATION_PROPERTY.getOrElse(3 hour)
  val UserCount = Properties.envOrElse("USER_COUNT", "100").toInt
  val RandomlySentMails = 10
  val NumberOfMailboxes = 10
  val NumberOfMessages = 20

}
