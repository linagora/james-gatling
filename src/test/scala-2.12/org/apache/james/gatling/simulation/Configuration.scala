package org.apache.james.gatling.simulation

import java.net.URL

import scala.concurrent.duration._
import scala.util.Properties

object Configuration {

  val ServerHostName = Properties.envOrElse("TARGET_HOSTNAME", "localhost")
  val JmapServerHostName = Properties.envOrElse("JMAP_TARGET_HOSTNAME", ServerHostName)
  val ImapServerHostName = Properties.envOrElse("IMAP_SERVER_HOSTNAME", ServerHostName)
  val WebadminServerHostName = Properties.envOrElse("WEBADMIN_SERVER_HOSTNAME", ServerHostName)

  val JMAP_PORT = Properties.envOrElse("JMAP_PORT", "1080").toInt
  val JMAP_PROTOCOL = Properties.envOrElse("JMAP_PROTOCOL", "http")
  val BaseJmapUrl = s"$JMAP_PROTOCOL://$JmapServerHostName:$JMAP_PORT"

  val WEBADMIN_PORT = Properties.envOrElse("WEBADMIN_PORT", "8000").toInt
  val WEBADMIN_PROTOCOL = Properties.envOrElse("WEBADMIN_PROTOCOL", "http")
  val BaseJamesWebAdministrationUrl = new URL(s"$WEBADMIN_PROTOCOL://$WebadminServerHostName:$WEBADMIN_PORT")

  val ScenarioDuration = 3 hours
  val UserCount = 1000
  val RandomlySentMails = 10
  val NumberOfMailboxes = 10
  val NumberOfMessages = 20

}
