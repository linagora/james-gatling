package org.apache.james.gatling.jmap.scenari

sealed trait RealUsageScenario {
  def name : String
}

case object InboxHomeLoading extends RealUsageScenario {
  def name = "InboxHomeLoading"
}

case object OpenMessage extends RealUsageScenario {
  def name = "OpenMessage"
}
case object SelectMailbox extends RealUsageScenario {
  def name = "SelectMailbox"
}
