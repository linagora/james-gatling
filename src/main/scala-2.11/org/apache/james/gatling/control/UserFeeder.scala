package org.apache.james.gatling.control

import io.gatling.core.Predef._

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

object UserFeeder {

  type UserFeeder = Array[Map[String, String]]

  val USERNAME_SESSION_PARAM: String = "username"
  val PASSWORD_SESSION_PARAM: String = "password"

  def createUserFeeder(userCount: Int): UserFeeder =
    toFeeder(
      awaitInitialization(
        createRegisteredUserFutures(userCount)))

  def createUserFeederWithInboxAndOutbox(userCount: Int): UserFeeder =
    toFeeder(
      awaitInitialization(
        createRegisteredUserFutures(userCount)
          .map(userFuture => userFuture.andThen {
            case Success(user) => registerSystemMailboxes(user)
          })))

  private def awaitInitialization(userFutures: List[Future[User]]) =
    Await.result(
      Future.sequence(userFutures),
      Inf)

  private def createRegisteredUserFutures(userCount: Int): List[Future[User]] = {
    val domain = Domain.random
    JamesWebAdministration.addDomain(domain).get

    generateUsers(userCount, domain)
      .map(JamesWebAdministration.addUser)
  }

  private def generateUsers(userCount: Int, domain: Domain): List[User] =
    (0 until userCount)
      .map(i => User.random(domain))
      .toList

  def registerSystemMailboxes(user: User): Future[User] = {
    Future.sequence(
      List(JamesWebAdministration.createInbox(user.username),
        JamesWebAdministration.createOutbox(user.username),
        JamesWebAdministration.createSentBox(user.username)))
      .map(responseList => user)
  }

  private def toFeeder(users: List[User]): UserFeeder =
    users.map(user => Map(USERNAME_SESSION_PARAM -> user.username.value, PASSWORD_SESSION_PARAM -> user.password.value)).toArray

}
