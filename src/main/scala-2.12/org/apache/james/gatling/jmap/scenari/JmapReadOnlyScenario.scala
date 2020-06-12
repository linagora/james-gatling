/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 * http://www.apache.org/licenses/LICENSE-2.0                   *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.gatling.jmap.scenari

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import org.apache.james.gatling.control.AuthenticatedUserFeeder.AuthenticatedUserFeederBuilder
import org.apache.james.gatling.jmap.JmapMessages.openpaasListMessageParameters
import org.apache.james.gatling.jmap.{JmapChecks, JmapMailbox, JmapMessages}

import io.gatling.core.session.Expression
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.concurrent.duration._

class JmapReadOnlyScenario {

  private object Keys {
    val inbox = "inboxID"
    val messageIds = "messageIds"
  }

  private object Queries {
    val getMailboxes: HttpRequestBuilder =
      JmapMailbox.getMailboxes
        .check((JmapMailbox.getMailboxesChecks ++ JmapMailbox.saveInboxAs(Keys.inbox)) : _*)

    val getMessagesList: HttpRequestBuilder =
      JmapMessages.listMessages(openpaasListMessageParameters(Keys.inbox))
        .check(JmapMessages.listMessagesChecks: _*)

    val getMessages: HttpRequestBuilder =
      JmapMessages.getMessages(JmapMessages.previewMessageProperties, Keys.messageIds)
        .check(isSuccess: _*)
  }

  private val isSuccess: Seq[HttpCheck] = Seq(
    status.is(200),
    JmapChecks.noError)


  private val pollTimeout: FiniteDuration = 30 seconds
  private val safetyMargin: FiniteDuration = 1 second

  val poll : ChainBuilder = exec(Queries.getMessagesList)
    .exec((session: Session) => session.set("poll", pollTimeout.fromNow))
  val shouldPoll: Expression[Boolean] = (session: Session) => {
    session.contains("poll") && session("poll").as[Deadline].timeLeft <= safetyMargin
  }

  def generate(userFeeder: AuthenticatedUserFeederBuilder, duration: Duration): ScenarioBuilder = {
    scenario("JmapReadOnlyScenario")
      .feed(userFeeder)
      .during(duration) {
        group(InboxHomeLoading.name)(
          pause(1 second)
          .doIf(shouldPoll) {
            exec(poll)
          }
            .exec(Queries.getMailboxes)
            .exec(Queries.getMessagesList)
            .exec(Queries.getMessages))
      }
  }

}
