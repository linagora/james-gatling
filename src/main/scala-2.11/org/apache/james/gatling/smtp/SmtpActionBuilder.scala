package org.apache.james.gatling.smtp

import io.gatling.core.action.{Action, ExitableActorDelegatingAction}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import org.apache.james.gatling.smtp.SmtpProtocol.SmtpComponents
import io.gatling.core.util.NameGen

case class SmtpActionBuilder(requestName: String,
                             _subject: String,
                             _body: String) extends ActionBuilder with NameGen {

  def subject(subject: String) = copy(_subject = subject)
  def body(body: String) = copy(_body = body)

  override def build(ctx: ScenarioContext, next: Action): Action = {
    val components: SmtpComponents = ctx.protocolComponentsRegistry.components(SmtpProtocol.SmtpProtocolKey)

    val smtpProps = SmtpAction.props(requestName, _subject, _body, ctx.coreComponents.statsEngine, next, components.protocol)
    val actionActor = ctx.system.actorOf(smtpProps)
    new ExitableActorDelegatingAction(genName(requestName), ctx.coreComponents.statsEngine, next, actionActor)
  }
}