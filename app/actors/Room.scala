package actors

import akka.actor._
import Messages._

class Room extends Actor {
  var roomListeners :Set[ActorRef] = Set.empty
  def receive = {
    case InviteRequest => roomListeners = roomListeners + sender()
    case msg: ChatMessage => roomListeners.foreach(_ ! msg)
  }
}
