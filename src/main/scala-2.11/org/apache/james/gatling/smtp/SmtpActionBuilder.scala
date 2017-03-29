package org.apache.james.gatling.smtp

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import org.apache.james.gatling.smtp.SmtpProtocol.SmtpComponents

case class SmtpActionBuilder(requestName: String,
                             _subject: String,
                             _body: String) extends ActionBuilder {

  def subject(subject: String) = copy(_subject = subject)
  def body(body: String) = copy(_body = body)

  override def build(ctx: ScenarioContext, next: Action): Action = {
    val components: SmtpComponents = ctx.protocolComponentsRegistry.components(SmtpProtocol.SmtpProtocolKey)

    new SmtpAction(requestName, _subject, _body, ctx.coreComponents.statsEngine, next, components.protocol)
  }
}