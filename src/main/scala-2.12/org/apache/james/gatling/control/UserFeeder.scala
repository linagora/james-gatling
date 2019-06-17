package org.apache.james.gatling.control

object UserFeeder {

  type UserFeeder = Array[Map[String, String]]

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
