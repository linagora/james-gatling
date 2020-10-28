/*
ISSUE-84: 404 on trying to fetch the mail queue 'spool' with the upgraded James memory image to branch-master.
It seems there is potentially a bug where we loose the mail queues on memory version lately.
Please fix it on James before re-enabling this test.

Bug reference on James: https://github.com/linagora/james-project/issues/3959
 */

//package org.apache.james.gatling.jmap.draft
//
//import org.apache.james.gatling.Fixture
//import org.apache.james.gatling.control.JamesWebAdministrationQuery
//import org.apache.james.gatling.jmap.draft.scenari.JmapQueueBrowseScenario
//
//import scala.concurrent.duration._
//
//class JmapQueueBrowseIT extends JmapIT {
//
//  var webAdmin = new JamesWebAdministrationQuery(server.mappedWebadmin.baseUrl)
//
//  before {
//    users.foreach(server.sendMessage(Fixture.homer.username))
//  }
//
//  scenario((userFeederBuilder, recipientFeederBuilder) => {
//    new JmapQueueBrowseScenario().generate(10 seconds, userFeederBuilder, recipientFeederBuilder, webAdmin)
//  })
//}
