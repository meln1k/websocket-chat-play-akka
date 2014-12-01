package actors

import java.util.Locale

import akka.actor._
import Messages._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import Constants._

object UserSession {
  def props(out: ActorRef, room: ActorRef) = Props(new UserSession(out, room))
}

class UserSession(out: ActorRef, room: ActorRef) extends Actor with ActorLogging {
  
  var userName = ""

  def receive = {
    case InMsg(SendMessage, text) => send2Room(text)
    case InMsg(SetName, userName) => requestNewName(userName)
    case InviteAccepted(name) => setName(name)
    case InviteDenied => send2Out(s"taken")
    case ChatMessage(name, text) => send2Out(text, Some(name))
    case Broadcast(text) => send2Out(text)
    case unhandled => log.warning(s"Unhandled message: ${unhandled.toString}")
  }

  def requestNewName(name: String) = room ! InviteRequest(name)
  def send2Room(msg: String) = room ! ChatMessage(userName, msg)
  def send2Out(msg: String, from: Option[String] = None) = out ! OutMsg(currentTime, from.getOrElse("system"), msg)
  def setName(name: String) = {
    userName = name
    send2Out("welcome")
  }

  val formatter = DateTimeFormat.forPattern("HH:mm:ss").withLocale(Locale.US)
  def currentTime = DateTime.now().toString(formatter)
  
  override def postStop() = room ! Unsubscribe
}