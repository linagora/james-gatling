package org.apache.james.gatling.simulation

import java.net.URL

import scala.concurrent.duration._
import scala.util.Properties

object Configuration {

  val ServerHostName = Properties.envOrElse("TARGET_HOSTNAME", "localhost")
  val BaseJmapUrl = s"http://$ServerHostName:${Properties.envOrElse("JMAP_PORT", "1080")}"
  val BaseJamesWebAdministrationUrl = new URL(s"http://$ServerHostName:${Properties.envOrElse("WEBADMIN_PORT", "8000")}")

  val ScenarioDuration = 3 hours
  val UserCount = 1000
  val RandomlySentMails = 10
  val NumberOfMailboxes = 10
  val NumberOfMessages = 20

}
