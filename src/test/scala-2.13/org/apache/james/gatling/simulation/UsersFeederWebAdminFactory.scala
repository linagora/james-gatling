package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.control.{RecipientFeeder, User, UserCreator, UserFeeder}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

class UsersFeederWebAdminFactory(userCount: Int = Configuration.UserCount) {

  var users: Seq[User] = Seq.empty

  def initUsers: UsersFeederWebAdminFactory = {
    users = Await.result(
      awaitable = Future.sequence(
        new UserCreator(Configuration.BaseJamesWebAdministrationUrl, Configuration.BaseJmapUrl).createUsersWithInboxAndOutbox(userCount)),
      atMost = Inf)
    this
  }

  def userFeeder(): UserFeederBuilder = UserFeeder.toFeeder(users).circular

  def recipientFeeder(): RecipientFeederBuilder = RecipientFeeder.usersToFeeder(users)

}
