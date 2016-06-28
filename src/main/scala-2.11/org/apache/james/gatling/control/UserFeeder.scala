package org.apache.james.gatling.control

import java.util.UUID

import io.gatling.core.Predef._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserFeeder {

  val USERNAME: String = "username"
  val PASSWORD: String = "password"

  def createUserFeeder(userCount: Int): Array[Map[String, String]] = {
    toFeeder(createRegisteredUsers(userCount))
  }

  def createUserFeederWithInboxAndOutbox(userCount: Int): Array[Map[String, String]] = {
    toFeeder(
      decorateWithSystemMailboxes(
        createRegisteredUsers(userCount)))
  }

  def decorateWithSystemMailboxes(users: List[User]): List[User] = {
    Future.sequence(
      users.map(user => Future.sequence(
        List(JamesWebAdministration.createInbox(user.username),
          JamesWebAdministration.createOutbox(user.username),
          JamesWebAdministration.createSentBox(user.username))))
    ).get
    users
  }

  def createRegisteredUsers(userCount: Int): List[User] = {
    val domain = Domain(UUID.randomUUID().toString)
    JamesWebAdministration.addDomain(domain).get

    registerUsers(generateUsers(userCount, domain))
  }

  def generateUsers(userCount: Int, domain: Domain): List[User] = {
    (0 until userCount)
      .map(i => User(
        Username(s"""${UUID.randomUUID().toString}@${domain.value}"""),
        Password(UUID.randomUUID().toString)))
      .toList
  }

  def registerUsers(users: List[User]): List[User] = {
    Future.sequence(
      users.map(user => JamesWebAdministration.addUser(user)))
      .get
    users
  }

  def toFeeder(users: List[User]): Array[Map[String, String]] = {
    users.map(user => Map(USERNAME -> user.username.value, PASSWORD -> user.password.value)).toArray
  }

}
