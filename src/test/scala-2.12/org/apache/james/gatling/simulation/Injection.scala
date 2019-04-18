package org.apache.james.gatling.simulation

import io.gatling.core.Predef
import io.gatling.core.Predef.constantUsersPerSec

import scala.concurrent.duration._

object Injection {

  private def toUserPerSec(userPerHour: Double): Double = userPerHour / 1.hour.toSeconds

  def constantUserPerHour(nb: Double): Predef.ConstantRateBuilder = constantUsersPerSec(toUserPerSec(nb))

}
