package org.apache.james.gatling.simulation

import io.gatling.core.Predef
import io.gatling.core.Predef.constantUsersPerSec

import scala.concurrent.duration._

abstract sealed class UsersDensity {
  def constantUserPerHour: Predef.ConstantRateBuilder
}
case class UsersPerHour(nb: Double) extends UsersDensity {
  private def toUserPerSec(userPerHour: Double): Double = userPerHour / 1.hour.toSeconds
  override def constantUserPerHour: Predef.ConstantRateBuilder = constantUsersPerSec(toUserPerSec(nb))
}
case class UsersPerSecond(nb: Double) extends UsersDensity {
  override def constantUserPerHour: Predef.ConstantRateBuilder = constantUsersPerSec(nb)
}
