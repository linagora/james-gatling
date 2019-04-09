package org.apache.james.gatling.control

object UserFeeder {

  type UserFeeder = Array[Map[String, String]]

  val UsernameSessionParam = "username"
  val PasswordSessionParam = "password"

  def toFeeder(users: Seq[User]): UserFeeder =
    users
      .map(user =>
        Map(
          UsernameSessionParam -> user.username.value,
          PasswordSessionParam -> user.password.value))
      .toArray
}
