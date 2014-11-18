package actors

object Messages {
  case object InviteRequest
  case class ChatMessage(text:String)
}
