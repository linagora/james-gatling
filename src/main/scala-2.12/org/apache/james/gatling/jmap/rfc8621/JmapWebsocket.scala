package org.apache.james.gatling.jmap.rfc8621

import io.gatling.core.Predef._
import io.gatling.http.Predef.ws
import io.gatling.http.action.ws.{WsCloseBuilder, WsSendTextFrameBuilder}
import io.gatling.http.request.builder.ws.WsConnectRequestBuilder

object JmapWebsocket {
  def websocketConnect(): WsConnectRequestBuilder =
    ws("Open websocket")
      .connect("/jmap/ws")
      .headers(JmapHttp.HEADERS_JSON)
      .basicAuth("${username}", "${password}")

  def websocketClose(): WsCloseBuilder =
    ws("Close websocket")
      .close

  def enablePush: WsSendTextFrameBuilder =
    ws("Enable push")
      .sendText("""{
                  |  "@type": "WebSocketPushEnable",
                  |  "dataTypes": ["Mailbox", "Email"]
                  |}""".stripMargin)

  def disablePush: WsSendTextFrameBuilder =
    ws("Disable push")
      .sendText("""{
                  |  "@type": "WebSocketPushDisable"
                  |}""".stripMargin)

  def setMailboxesWs: WsSendTextFrameBuilder =
    ws("setMailboxesWs")
      .sendText(s"""{
                   |  "@type": "Request",
                   |  "using": ["urn:ietf:params:jmap:core","urn:ietf:params:jmap:mail"],
                   |  "methodCalls": [[
                   |    "Mailbox/set",
                   |    {
                   |      "accountId": "$${accountId}",
                   |      "create": {
                   |        "$${createdId}": {
                   |          "name": "$${mailboxName}"
                   |        }
                   |      }
                   |    },
                   |    "c1"]]
                   |}""".stripMargin)
}
