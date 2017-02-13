package akkaCluster

import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem, Address}
import akka.pattern.ask
import akka.util.Timeout
import akkaCluster.ClusterObject.{ClaudeNotReadyException, PriceDecisionNotReady, WhatIsClaudeAddress, WhatIsPriceDecisionAddress}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class OtherAppsAddresses @Inject() (actorSystem: ActorSystem, implicit val ec: ExecutionContext) {
  implicit val timeout: Timeout = 3.seconds

  def claudeAddress: Future[Option[Address]] = actorSystem
    .actorSelection(ClusterObject.path)
    .resolveOne()
    .flatMap (actorRef => (actorRef ? WhatIsClaudeAddress).mapTo[Option[Address]])

  def priceDecisionAddress: Future[Option[Address]] = actorSystem.actorSelection(ClusterObject.path)
    .resolveOne()
    .flatMap (_ ? WhatIsPriceDecisionAddress)
    .mapTo[Option[Address]]

  def claudeEventActor: Future[ActorRef] = claudeAddress.flatMap {
    case Some(address) =>
      actorSystem
        .actorSelection(s"$address/user/event-actor")
        .resolveOne()

    case None =>
      throw new ClaudeNotReadyException
  }

  def priceDecisionCountsActor: Future[ActorRef] = priceDecisionAddress.flatMap {
    case Some(address) =>
      actorSystem
        .actorSelection(s"$address/user/get-counts")
        .resolveOne()

    case None =>
      throw new PriceDecisionNotReady
  }

  def whereDoesMyPublicGoActor: Future[ActorRef] = priceDecisionAddress.flatMap {
    case Some(address) =>
      actorSystem
        .actorSelection(s"$address/user/where-does-my-public-go")
        .resolveOne()

    case None =>
      throw new PriceDecisionNotReady
  }
}

class OtherAppsAddressesMock @Inject() (actorSystem: ActorSystem, ec: ExecutionContext, claudeEventActorRef: ActorRef)
    extends OtherAppsAddresses(actorSystem, ec) {
  override def claudeEventActor: Future[ActorRef] = Future.successful(claudeEventActorRef)
}
