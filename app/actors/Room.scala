package actors

import akka.actor._
import Messages._

class Room extends Actor {
  val sessions : collection.mutable.Map[String,ActorRef] = collection.mutable.Map.empty
  def receive = {
    case InviteRequest(name) =>
      if (sessions.isDefinedAt(name)) {
        sender() ! InviteDenied(name)
      } else {
        sessions += name -> sender()
        sender() ! InviteAccepted(name)
        sendAll(UserConnected(name))
      }
    case msg: ChatMessage => sendAll(msg)
    case Unsubscribe(name) =>
      sessions.remove(name)
      sendAll(Broadcast(s"$name has left."))
  }

  def sendAll(msg: Any) = sessions.values.foreach(_ ! msg)
}
