package org.apache.james.gatling.jmap.scenari.common

import scala.concurrent.duration._
import java.net.URL

object Configuration {

  val ServerHostName = "127.0.0.1"
  val BaseJmapUrl = s"http://$ServerHostName:1080"
  val BaseJamesWebAdministrationUrl = new URL(s"http://$ServerHostName:8000")

  val ScenarioDuration = 3 hours
  val UserCount = 100
  val RandomlySentMails = 10

}
