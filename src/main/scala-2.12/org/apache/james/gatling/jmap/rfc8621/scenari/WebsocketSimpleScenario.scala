package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.draft.CommonSteps.provisionSystemMailboxes
import org.apache.james.gatling.jmap.draft.{MailboxId, MailboxName}
import org.apache.james.gatling.jmap.rfc8621.JmapWebsocket.{enablePush, setMailboxesWs, websocketClose, websocketConnect}
import org.apache.james.gatling.jmap.rfc8621.SessionStep

import scala.concurrent.duration._

class WebsocketSimpleScenario {
  def generate(duration: Duration, userFeeder: UserFeederBuilder): ScenarioBuilder =
    scenario("WebsocketSimpleScenario")
      .feed(userFeeder)
      .exec(SessionStep.retrieveAccountId)
      .exec(provisionSystemMailboxes())
      .exec(websocketConnect.onConnected(
        exec(enablePush)
          .during(duration) {
            exec(createMailbox)
              .pause(2 second)
        }))
//      .exec(websocketClose)

  def createMailbox: ChainBuilder =
    exec((session: Session) => session.set("createdId", MailboxId.generate().id))
      .exec((session: Session) => session.set("mailboxName", MailboxName.generate().name))
      .exec(setMailboxesWs
        .await(2 seconds)(
          ws.checkTextMessage("check mailbox created push state").check(jsonPath("$.@type").is("StateChange")))
        .await(2 seconds)(
          ws.checkTextMessage("check mailbox subscribed push state").check(jsonPath("$.@type").is("StateChange")))
        .await(2 seconds)(
          ws.checkTextMessage("check mailbox/set response").check(jsonPath("$.methodResponses[0][1].created").find.saveAs("mailboxCreated"))))
}
