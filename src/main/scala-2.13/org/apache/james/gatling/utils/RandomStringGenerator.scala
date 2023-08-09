package org.apache.james.gatling.utils

import java.util.UUID

import scala.util.Random

object RandomStringGenerator {
  def randomString: String = UUID.randomUUID().toString

  def randomAlphaString(length: Int = 5): String = Random.alphanumeric.take(length).mkString("")
}
