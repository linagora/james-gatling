package org.apache.james.gatling

import java.io.{BufferedReader, InputStreamReader}

import org.apache.james.gatling.jmap.scenari.JmapInboxHomeLoadingScenario

class JmapInboxHomeLoadingIT extends JmapIT {
  before {

    val emlPath = getClass.getResource("/message.eml").getPath
    val shPath = getClass.getResource("/add_mail.sh").getPath

    {
      val rt = Runtime.getRuntime
      val copyCommand = s"docker cp  ${emlPath} ${server.containerId}:/message.eml"
      val copyPr = rt.exec(copyCommand)
      copyPr.waitFor()
    }
    {
      val rt = Runtime.getRuntime
      val copyCommand = s"docker cp ${shPath} ${server.containerId}:/add_mail.sh"
      val copyPr = rt.exec(copyCommand)
      copyPr.waitFor()
    }
    {
      val rt = Runtime.getRuntime
      val importMailCommand = s"docker exec ${server.containerId}  /add_mail.sh"
      val pr = rt.exec(importMailCommand)
      pr.waitFor()
      Thread.sleep(1000)
    }
  }
  scenario(feederBuilder => new JmapInboxHomeLoadingScenario().generate(feederBuilder))
}
