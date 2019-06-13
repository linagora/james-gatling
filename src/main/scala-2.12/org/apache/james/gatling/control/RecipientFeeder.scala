package org.apache.james.gatling.control

import org.apache.james.gatling.jmap.RecipientAddress

object RecipientFeeder {

  type RecipientFeeder = Array[Map[String, String]]

  val RecipientSessionParam = "recipient"

  def toFeeder(recipients: Seq[RecipientAddress]): RecipientFeeder =
    recipients
      .map(recipient =>
        Map(
          RecipientSessionParam -> recipient.address))
      .toArray

  def usersToFeeder(users: Seq[User]): RecipientFeeder =
    users
      .map(user =>
        Map(
          RecipientSessionParam -> user.username.value))
      .toArray
}
