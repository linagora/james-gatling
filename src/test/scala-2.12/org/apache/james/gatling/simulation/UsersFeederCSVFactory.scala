package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import org.apache.james.gatling.control.RecipientFeeder.RecipientFeederBuilder
import org.apache.james.gatling.control.UserFeeder.UserFeederBuilder
import org.apache.james.gatling.control.{Password, RecipientFeeder, User, UserFeeder, Username}

class UsersFeederCSVFactory(csvFileName: String = "users.csv") {

  private def recordValueToString(recordValue: Any): String = recordValue match {
    case s: String => s
    case a: Any => println("Warning: calling toString on a feeder value"); a.toString
  }

  var users: Seq[User] = Seq.empty

  def loadUsers: UsersFeederCSVFactory = {
    users = csv(csvFileName).readRecords
      .map(record =>
        User(
          username = Username(recordValueToString(record("username"))),
          password = Password(recordValueToString(record("password"))))
      )
    this
  }

  def userFeeder(): UserFeederBuilder = UserFeeder.toFeeder(users).circular

  def recipientFeeder(): RecipientFeederBuilder = RecipientFeeder.usersToFeeder(users)

}