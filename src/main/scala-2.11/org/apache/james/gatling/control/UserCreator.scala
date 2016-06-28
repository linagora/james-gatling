package org.apache.james.gatling.control

import io.gatling.core.Predef._

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.concurrent.Awaitable

object UserCreator {

  def createUsersWithInboxAndOutbox(userCount: Int): Seq[Future[User]] =
        createUsers(userCount)
          .map(userFuture => userFuture.andThen {
            case Success(user) => registerSystemMailboxes(user)
          })

  private def createUsers(userCount: Int): Seq[Future[User]] = {
    val domain = Domain.random
    JamesWebAdministration.addDomain(domain).get

    generateUsers(userCount, domain)
      .map(JamesWebAdministration.addUser)
  }

  private def generateUsers(userCount: Int, domain: Domain): Seq[User] =
    (0 until userCount)
      .map(i => User.random(domain))

  def registerSystemMailboxes(user: User): Future[User] = {
    Future.sequence(
      List(JamesWebAdministration.createInbox(user.username),
        JamesWebAdministration.createOutbox(user.username),
        JamesWebAdministration.createSentBox(user.username)))
      .map(responseList => user)
  }
}
