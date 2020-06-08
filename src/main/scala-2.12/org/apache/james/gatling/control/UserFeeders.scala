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

object AuthenticatedUserFeeder {

  type AuthenticatedUserFeeder = Iterator[Map[String, String]]

  type AuthenticatedUserFeederBuilder = () => Feeder[Any]

  val usernameSessionParam = "username"
  val jwtAccessTokenSessionParam = "jwtAccessToken"

  def toFeeder(users: Iterator[AuthenticatedUser]): AuthenticatedUserFeeder =
    users
      .map(user =>
        Map(
          usernameSessionParam -> user.username.value,
          jwtAccessTokenSessionParam -> user.jwtAccessToken.value))
}
