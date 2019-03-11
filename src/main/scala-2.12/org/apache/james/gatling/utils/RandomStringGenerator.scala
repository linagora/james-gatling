package org.apache.james.gatling.utils

import java.util.UUID

object RandomStringGenerator {
  def randomString = UUID.randomUUID().toString;
}
