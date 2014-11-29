package actors

import java.util.Locale

import akka.actor._
import Messages._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object UserSession {
  def props(out: ActorRef, room: ActorRef) = Props(new UserSession(out, room))
}

class UserSession(out: ActorRef, room: ActorRef) extends Actor with ActorLogging{
  val nameRegex = """^:name\s*(\w+)""".r

  var name: Option[String] = None

  val formatter = DateTimeFormat.forPattern("HH:mm:ss").withLocale(Locale.US)
  def currentTime = DateTime.now().toString(formatter)

  def receive = {
    case InMsg(msg) =>
      name.fold{
      msg match {
        case nameRegex(userName) =>
          room ! InviteRequest(userName)
        case _ => out ! OutMsg(currentTime, "system", "Please set your name by using command :name")
      }
    } { userName =>
      room ! ChatMessage(userName, msg)
    }

    case InviteDenied(userName) => out ! OutMsg(currentTime, "system", s"Name $userName is already taken.")

    case InviteAccepted(userName) =>
      name = Some(userName)
      out !  OutMsg(currentTime, "system", s"Welcome, $userName.")

    case UserConnected(userName) => if (userName != name.get) out ! OutMsg(currentTime, "system", s"User $userName connected!")

    case ChatMessage(name,text) => out ! OutMsg(currentTime, name, text)
    case Broadcast(text) => out !  OutMsg(currentTime, "system", text)
  }

  override def postStop() = {
    name.map(n => room ! Unsubscribe(n))
  }
}