package actors

object Messages {
  case class InviteRequest(name: String)
  case class InviteDenied(name: String)
  case class InviteAccepted(name: String)
  case class Unsubscribe(name: String)
  case class Broadcast(text: String)
  case class UserConnected(text: String)
  case class ChatMessage(userName: String, text: String)
}
