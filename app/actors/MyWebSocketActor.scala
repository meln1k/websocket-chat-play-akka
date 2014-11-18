package actors

import akka.actor._
import Messages._

object MyWebSocketActor {
  def props(out: ActorRef, room: ActorRef) = Props(new MyWebSocketActor(out, room))
}

class MyWebSocketActor(out: ActorRef, room: ActorRef) extends Actor {
  room ! InviteRequest
  def receive = {
    case msg: String => room ! ChatMessage(msg)
    case ChatMessage(text) => out ! text
  }
}