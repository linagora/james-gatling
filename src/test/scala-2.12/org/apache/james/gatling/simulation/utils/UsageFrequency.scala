package org.apache.james.gatling.simulation.utils

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

case class UsageFrequency(nbUsagePerHourPerUser: Int, nbUser: Int) {
  def paceFromFrequency: Duration = {
    Duration(nbUsagePerHourPerUser.toLong * nbUser, TimeUnit.HOURS)
  }
}

object UsageFrequency {
  val ONE_TIME_PER_USER_PER_HOUR_FOR_FIFTY_THOUSANDS_USERS = UsageFrequency(1, 50000)
}
