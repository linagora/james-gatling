package org.apache.james.gatling.control

import io.gatling.core.Predef._

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.concurrent.Awaitable

object UserFeeder {

  type UserFeeder = Array[Map[String, String]]

  val USERNAME_SESSION_PARAM: String = "username"
  val PASSWORD_SESSION_PARAM: String = "password"

  def createCompletedUserFeederWithInboxAndOutbox(userCount: Int): UserFeeder =
    await(toFeeder(UserCreator.createUsersWithInboxAndOutbox(userCount)))

  private def await[T](f: Awaitable[T]): T = Await.result(f, Inf)

  private def toFeeder(users: Seq[Future[User]]): Future[UserFeeder] =
    Future.sequence(users)
      .map(users => users.map(user => Map(USERNAME_SESSION_PARAM -> user.username.value, PASSWORD_SESSION_PARAM -> user.password.value)).toArray)
}
