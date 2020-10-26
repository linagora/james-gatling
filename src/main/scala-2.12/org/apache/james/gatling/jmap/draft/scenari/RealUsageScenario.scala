package org.apache.james.gatling.jmap.draft.scenari

sealed trait RealUsageScenario {
  def name : String
}

case object InboxHomeLoading extends RealUsageScenario {
  def name = "InboxHomeLoading"
}

case object DefaultInboxSearchLoading extends RealUsageScenario {
  def name = "DefaultInboxSearchLoading"
}

case object OpenMessage extends RealUsageScenario {
  def name = "OpenMessage"
}

case object SelectMailbox extends RealUsageScenario {
  def name = "SelectMailbox"
}

case object ListMessageBodyStructure extends RealUsageScenario {
  def name = "ListMessageBodyStructure"
}

case object MessageFlagUpdates extends RealUsageScenario {
  def name = "MessageFlagUpdates"
}
