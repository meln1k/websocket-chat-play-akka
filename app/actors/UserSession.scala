package actors

import java.util.Locale

import akka.actor._
import Messages._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import Constants._

object UserSession {
  def props(out: ActorRef, room: ActorRef) = Props(classOf[UserSession], out, room)
}

class UserSession(out: ActorRef, room: ActorRef) extends Actor with ActorLogging {
  
  var userName: Option[String] = None

  def receive = {
    case InMsg(SendMessage, text) => send2Room(text)
    case InMsg(SetName, userName) => requestNewName(userName)
    case InviteAccepted(name) => setName(name)
    case InviteDenied => send2Out("taken", Some("system"))
    case ChatMessage(name, text) => send2Out(text, Some(name))
    case Broadcast(text) => send2Out(text)
    case unhandled => log.warning(s"Unhandled message: ${unhandled.toString}")
  }

  def requestNewName(name: String) = if (userName.isEmpty) room ! InviteRequest(name)
  def send2Room(msg: String) = userName.foreach(name => room ! ChatMessage(name, msg))
  def send2Out(msg: String, from: Option[String] = None) = out ! OutMsg(currentTime, from.getOrElse("toAll"), msg)
  def setName(name: String) = {
    userName = Some(name)
    send2Out("welcome", Some("system"))
  }

  val formatter = DateTimeFormat.forPattern("HH:mm:ss").withLocale(Locale.US)
  def currentTime = DateTime.now().toString(formatter)
  
  override def postStop() = userName.map(name => room ! Unsubscribe(name))
}