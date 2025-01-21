package org.apache.james.gatling.jmap.rfc8621.scenari

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.jmap.rfc8621.scenari.DownloadAttachmentScenario.{emailGetAttachmentsProperties, emailWithAttachmentsIdsKey}
import org.apache.james.gatling.jmap.rfc8621.{JmapEmail, JmapHttp, JmapMailbox, SessionStep}

import scala.concurrent.duration._
import scala.util.Properties

object DownloadAttachmentScenario {
  val emailGetAttachmentsProperties = "[\"attachments\"]"
  val emailWithAttachmentsIdsKey = "emailWithAttachmentsIds"
}

class DownloadAttachmentScenario {

  def generate(duration: Duration, userFeeder: UserFeederBuilder, recipientFeeder: RecipientFeederBuilder,
               provisionMailWithAttachments: Option[Boolean] = None): ScenarioBuilder = {

    val provisionMailWithAttachmentsValue: Boolean = provisionMailWithAttachments.getOrElse(Properties.envOrNone("PROVISION_MAIL_WITH_ATTACHMENTS") match {
      case Some(value) => value.toBoolean
      case _ => false
    })

    scenario("DownloadAttachmentScenario")
      .feed(userFeeder)
      .exec(SessionStep.retrieveAccountId)
      .doIf(_ => provisionMailWithAttachmentsValue) {
        exec(JmapMailbox.provisionUsersWithMessagesAndAttachment(recipientFeeder, numberOfMessages = 10))
      }
      .during(duration.toSeconds.toInt) {
        exec(JmapEmail.queryEmails(callName = "emailQuery hasAttachment", JmapEmail.filterAttachments())
          .check(JmapHttp.statusOk, JmapHttp.noError, nonEmptyListMessageIdsChecks()))
          .doIf(session => !session(emailWithAttachmentsIdsKey).asOption[Seq[String]].forall(_.isEmpty)) {
            pause(1 second, 2 seconds)
              .exec(JmapEmail.getRandomEmails(properties = emailGetAttachmentsProperties, emailIdsKey = emailWithAttachmentsIdsKey)
                .check(JmapHttp.statusOk, JmapHttp.noError, JmapEmail.nonEmptyEmailsChecks, nonEmptyBlobIdChecks()))
              .pause(1 second, 2 seconds)
              .exec(JmapHttp.download(callName = "Download Attachment")
                .check(JmapHttp.statusOk))
          }
      }
  }

  private def nonEmptyListMessageIdsChecks(): HttpCheck =
    jsonPath("$.methodResponses[0][1].ids[*]").findAll.optional.saveAs(emailWithAttachmentsIdsKey)

  private def nonEmptyBlobIdChecks(): HttpCheck =
    jsonPath("$.methodResponses[0][1].list[0].attachments[0].blobId").find.saveAs("blobId")
}

