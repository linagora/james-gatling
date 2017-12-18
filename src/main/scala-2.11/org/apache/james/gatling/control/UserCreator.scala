package org.apache.james.gatling.control

import java.net.URL

import io.gatling.core.Predef._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

class UserCreator(val baseJamesWebAdministrationUrl: URL) {
  private val jamesWebAdministration = new JamesWebAdministration(baseJamesWebAdministrationUrl)

  def createUsersWithInboxAndOutbox(userCount: Int): Seq[Future[User]] =
    createUsers(userCount)
      .map(userFuture => userFuture.andThen {
        case Success(user) => registerSystemMailboxes(user)
      })

  def createUsers(userCount: Int): Seq[Future[User]] = {
    val domain = Domain.random
    jamesWebAdministration.addDomain(domain).get

    generateUsers(userCount, domain)
      .map(jamesWebAdministration.addUser)
  }

  private def generateUsers(userCount: Int, domain: Domain): Seq[User] =
    (0 until userCount)
      .map(i => User.random(domain))

  def registerSystemMailboxes(user: User): Future[User] =
    Future.sequence(
      List(jamesWebAdministration.createInbox(user.username),
        jamesWebAdministration.createOutbox(user.username),
        jamesWebAdministration.createSentBox(user.username)))
      .map(responseList => user)
}
