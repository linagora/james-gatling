package org.apache.james.gatling.control

import io.gatling.core.Predef._

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.concurrent.Awaitable

object UserFeeder {

  type UserFeeder = Array[Map[String, String]]

  val UsernameSessionParam = "username"
  val PasswordSessionParam = "password"

  def createCompletedUserFeederWithInboxAndOutbox(users: Seq[Future[User]]): UserFeeder =
    await(toFeeder(users))

  private def await[A](f: Awaitable[A]): A = Await.result(f, Inf)

  private def toFeeder(users: Seq[Future[User]]): Future[UserFeeder] =
    Future.sequence(users)
      .map(users => users.map(user => Map(UsernameSessionParam -> user.username.value, PasswordSessionParam -> user.password.value)).toArray)
}
