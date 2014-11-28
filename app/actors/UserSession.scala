package actors

import akka.actor._
import Messages._
import org.joda.time.DateTime

object UserSession {
  def props(out: ActorRef, room: ActorRef) = Props(new UserSession(out, room))
}

class UserSession(out: ActorRef, room: ActorRef) extends Actor with ActorLogging{
  val nameRegex = """^:name\s*(\w+)""".r

  var name: Option[String] = None

  def receive = {
    case InMsg(msg) =>
      name.fold{
      msg match {
        case nameRegex(userName) =>
          room ! InviteRequest(userName)
        case _ => out ! OutMsg(DateTime.now().toString(), "system", "Please set your name by using command :name")
      }
    } { userName =>
      room ! ChatMessage(userName, msg)
    }

    case InviteDenied(userName) => out ! OutMsg(DateTime.now().toString(), "system", s"Name $userName is already taken.")

    case InviteAccepted(userName) =>
      name = Some(userName)
      out !  OutMsg(DateTime.now().formatted("HH:mm:ss"), "system", s"Welcome, $userName.")

    case UserConnected(userName) => if (userName != name.get) out ! OutMsg(DateTime.now().toString(), "system", s"User $userName connected!")

    case ChatMessage(name,text) => out ! OutMsg(DateTime.now().toString(), name, text)
    case Broadcast(text) => out !  OutMsg(DateTime.now().toString(), "system", text)
  }

  override def postStop() = {
    name.map(n => room ! Unsubscribe(n))
  }
}