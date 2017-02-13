package akkaCluster

import akka.actor.Address

object ClusterObject {
  val name = "akka-cluster"
  val path: String = "/user/" + name

  case object ClaudeDown
  case object PriceDecisionDown
  case object ClaudeNotReady

  case object WhatIsClaudeAddress
  case object WhatIsPriceDecisionAddress

  class ClaudeNotReadyException extends Exception
  class PriceDecisionNotReady extends Exception

  final case class PriceDecisionUp(address: Address)
  final case class ClaudeUp(address: Address)
}
