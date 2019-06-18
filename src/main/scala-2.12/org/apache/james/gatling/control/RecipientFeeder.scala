package org.apache.james.gatling.control

import io.gatling.core.feeder.Feeder
import org.apache.james.gatling.jmap.RecipientAddress

object RecipientFeeder {

  type RecipientFeeder = Array[Map[String, String]]

  type RecipientFeederBuilder = () => Feeder[Any]

  val recipientSessionParam = "recipient"

  def toFeeder(recipients: Seq[RecipientAddress]): RecipientFeeder =
    recipients
      .map(recipient =>
        Map(
          recipientSessionParam -> recipient.address))
      .toArray

  def usersToFeeder(users: Seq[User]): RecipientFeeder =
    users
      .map(user =>
        Map(
          recipientSessionParam -> user.username.value))
      .toArray
}
