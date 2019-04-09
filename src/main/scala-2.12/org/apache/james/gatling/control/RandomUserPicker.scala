package org.apache.james.gatling.control

import org.apache.james.gatling.jmap.CommonSteps.UserPicker

import scala.util.Random

object RandomUserPicker {
  def apply(users: Seq[User]): UserPicker = () =>
      users(Random.nextInt(users.length))
}
