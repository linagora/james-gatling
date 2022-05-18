package org.apache.james.gatling.control

import java.net.URL

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Success

class UserCreator(val baseJamesWebAdministrationUrl: URL, val baseJamesJmap: URL) {
  private val jamesWebAdministration = new JamesWebAdministration(baseJamesWebAdministrationUrl)
  private val jamesJmap = new JamesJmap(baseJamesJmap)

  def createUsersWithInboxAndOutbox(userCount: Int): Seq[Future[User]] =
    createUsers(userCount)
      .map(userFuture => userFuture.andThen {
        case Success(user) => registerSystemMailboxes(user)
      })

  def createUsersWithInboxAndOutbox(users: Seq[User], domain: Domain) = {
    Await.result(jamesWebAdministration.addDomain(domain), 10.seconds)

    createUsers(users)
      .map(userFuture => userFuture.andThen {
        case Success(user) => registerSystemMailboxes(user)
      })
  }

  def createUsers(userCount: Int): Seq[Future[User]] = {
    val domain = Domain.random
    Await.result(jamesWebAdministration.addDomain(domain), 10.seconds)

    generateUsers(userCount, domain)
      .map(jamesWebAdministration.addUser)
  }

  def createUsers(users: Seq[User]): Seq[Future[User]] =
    users.map(jamesWebAdministration.addUser)

  private def generateUsers(userCount: Int, domain: Domain): Seq[User] =
    (0 until userCount)
      .map(i => User.random(domain))

  def registerSystemMailboxes(user: User): Future[User] =
    Future.sequence(
      List(jamesWebAdministration.createInbox(user.username),
        jamesWebAdministration.createOutbox(user.username),
        jamesWebAdministration.createSentBox(user.username)))
      .map(responseList => user)

  def authenticateUser(user: User): Future[AuthenticatedUser] = {
    jamesJmap.authenticateUser(user)
  }
}
