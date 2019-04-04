package org.apache.james.gatling.simulation

import java.net.URL

import scala.concurrent.duration._

object Configuration {

  val ServerHostName = "127.0.0.1"
  val BaseJmapUrl = s"http://$ServerHostName:1080"
  val BaseJamesWebAdministrationUrl = new URL(s"http://$ServerHostName:8000")

  val ScenarioDuration = 3 hours
  val UserCount = 1000
  val RandomlySentMails = 10
  val NumberOfMailboxes = 10
  val NumberOfMessages = 20

}
