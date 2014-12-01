package actors

import actors.Constants.InMessageType

object Messages {
  sealed trait Message
  case class InMsg(messageType: InMessageType, messageText: String) extends Message
  case class OutMsg(time: String, from: String, messageText: String) extends Message
  case class InviteRequest(name: String) extends Message
  case object InviteDenied extends Message
  case class InviteAccepted(name: String) extends Message
  case class Unsubscribe(name: String) extends Message
  case class Broadcast(text: String) extends Message
  case class ChatMessage(from: String, text: String) extends Message
}
