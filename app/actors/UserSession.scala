package actors

import akka.actor._
import Messages._

object UserSession {
  def props(out: ActorRef, room: ActorRef) = Props(new UserSession(out, room))
}

class UserSession(out: ActorRef, room: ActorRef) extends Actor {
  val nameRegex = """^:name\s*(\w+)""".r

  var name: Option[String] = None

  def receive = {
    case msg: String => name.fold{
      msg match {
        case nameRegex(userName) =>
          room ! InviteRequest(userName)
        case _ => out ! "Please set your name by using command :name"
      }
    } { userName =>
      room ! ChatMessage(userName, msg)
    }

    case InviteDenied(userName) => out ! s"Name $userName is already taken."

    case InviteAccepted(userName) =>
      name = Some(userName)
      out ! s"Welcome, $userName."

    case UserConnected(userName) => if (userName != name.get) out ! s"User $userName connected!"

    case ChatMessage(name,text) => out ! s"$name: $text"
    case Broadcast(text) => out ! text
  }

  override def postStop() = {
    name.map(n => room ! Unsubscribe(n))
  }
}