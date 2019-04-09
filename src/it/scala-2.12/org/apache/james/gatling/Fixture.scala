package org.apache.james.gatling

import io.gatling.core.feeder.FeederBuilder
import org.apache.james.gatling.control.{Password, User, Username}

object Fixture {
  val bart = User(Username("bart@simpson.cartoon"), Password("Eat My Shorts"))
  val homer = User(Username("homer@simpson.cartoon"), Password("Mmm... donuts"))

  def feederBuilder(users: User*): FeederBuilder = () => {
    val feeder = users.map(user => Map("username" -> user.username, "password" -> user.password))
    feeder.toIterator
  }
}
