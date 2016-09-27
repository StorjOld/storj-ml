package actors

import akka.actor.{Actor, ActorRef, Props}

class EventListener(out: ActorRef, producer: ActorRef) extends Actor {
  override def preStart() = producer ! Subscribe
  override def postStop(): Unit = producer ! Unsubscribe

  def receive = {
    case msg: String => out ! msg
  }
}

object EventListener {
  def props(out: ActorRef, producer: ActorRef) = Props(new EventListener(out, producer))
}