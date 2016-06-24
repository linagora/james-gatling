package org.apache.james.gatling.control

import java.util.UUID

import io.gatling.core.Predef._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserFeeder {

  def createUserFeeder(userCount: Int): Array[Map[String, String]] = {
    val domain = Domain(UUID.randomUUID().toString)

    JamesWebAdministration.addDomain(domain).get

    val users = (0 until userCount)
      .map(i => User(
        Username(s"""${UUID.randomUUID().toString}@${domain.value}"""),
        Password(UUID.randomUUID().toString)))
      .toList

    Future.sequence(
      users.map(user => JamesWebAdministration.addUser(user)))
      .get

    users.map(user => Map("username" -> user.username.value, "password" -> user.password.value)).toArray
  }

}
