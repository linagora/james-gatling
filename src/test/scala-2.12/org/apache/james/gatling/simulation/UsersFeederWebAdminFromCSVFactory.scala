package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.control.{Domain, Password, RecipientFeeder, User, UserCreator, UserFeeder, Username}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

class UsersFeederWebAdminFromCSVFactory(csvFileName: String = "users.csv") {

  val domain: Domain = Domain("test.org")

  private def recordValueToString(recordValue: Any): String = recordValue match {
    case s: String => s
    case a: Any => println("Warning: calling toString on a feeder value"); a.toString
  }

  var users: Seq[User] = Seq.empty

  def loadUsers: UsersFeederWebAdminFromCSVFactory = {

    val usersFromCSV = csv(csvFileName).readRecords
      .map(record =>
        User(
          username = Username(recordValueToString(record("username"))),
          password = Password(recordValueToString(record("password"))))
      )

    users = Await.result(
      awaitable = Future.sequence(
        new UserCreator(Configuration.BaseJamesWebAdministrationUrl, Configuration.BaseJmapUrl).createUsersWithInboxAndOutbox(usersFromCSV, domain)),
      atMost = Inf)

    this
  }

  def userFeeder(): UserFeederBuilder = UserFeeder.toFeeder(users).circular

  def recipientFeeder(): RecipientFeederBuilder = RecipientFeeder.usersToFeeder(users)

}
