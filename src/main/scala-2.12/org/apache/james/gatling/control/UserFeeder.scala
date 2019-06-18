package org.apache.james.gatling.control

import io.gatling.core.feeder.Feeder

object UserFeeder {

  type UserFeeder = Array[Map[String, String]]

  type UserFeederBuilder = () => Feeder[Any]

  val usernameSessionParam = "username"
  val passwordSessionParam = "password"

  def toFeeder(users: Seq[User]): UserFeeder =
    users
      .map(user =>
        Map(
          usernameSessionParam -> user.username.value,
          passwordSessionParam -> user.password.value))
      .toArray
}
