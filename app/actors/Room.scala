package actors

import akka.actor._
import Messages._

class Room extends Actor with ActorLogging {

  val sessions : collection.mutable.Map[String,ActorRef] = collection.mutable.Map.empty

  def receive = {
    case InviteRequest(name) => tryRegisterUser(name)
    case msg: ChatMessage => sendAll(msg)
    case Unsubscribe(name) => unsubscribe(name)
    case unhandled => log.warning(s"Unhandled message: ${unhandled.toString}")
  }

  def tryRegisterUser(name: String) = {
    if (sessions.isDefinedAt(name)) {
      sender ! InviteDenied
    } else {
      sessions += name -> sender
      sender ! InviteAccepted(name)
      sendAll(Broadcast(name))
    }
  }

  def unsubscribe(name: String) = {
    sessions.remove(name)
    sendAll(Broadcast(s"$name has left."))
  }

  def sendAll(msg: Any) = sessions.values.foreach(_ ! msg)
}
