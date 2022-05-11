package org.apache.james.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.core.controller.inject.open._

import scala.concurrent.duration._

abstract sealed class UsersDensity {
  def injectDuring(givenDuring: FiniteDuration): OpenInjectionStep
}
case class UsersPerHour(nb: Double) extends UsersDensity {
  private val ONE_HOUR: Duration = 1 hour
  private val usersPerSec: Double = nb / ONE_HOUR.toSeconds
  override def injectDuring(givenDuring: FiniteDuration): ConstantRateOpenInjection = constantUsersPerSec(usersPerSec) during givenDuring
}
case class UsersPerSecond(nb: Double) extends UsersDensity {
  override def injectDuring(givenDuring: FiniteDuration): ConstantRateOpenInjection = constantUsersPerSec(nb) during givenDuring
}
case class UsersTotal(nb: Double) extends UsersDensity {
  override def injectDuring(givenDuring: FiniteDuration): OpenInjectionStep = rampUsers(nb.toInt) during givenDuring
}
