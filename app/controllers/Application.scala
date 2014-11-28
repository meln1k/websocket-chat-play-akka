package controllers

import actors.Messages.{OutMsg, InMsg}
import play.api._
import play.api.libs.json._
import play.api.mvc.WebSocket.FrameFormatter
import play.api.mvc._
import play.api.Play.current
import actors._


object Application extends Controller {

  implicit val InMsgFormat = Json.format[InMsg]
  implicit val OutMsgFormat = Json.format[OutMsg]

  implicit val InMsgFrameFormatter = FrameFormatter.jsonFrame[InMsg]
  implicit val OutMsgFrameFormatter = FrameFormatter.jsonFrame[OutMsg]

  def socket = WebSocket.acceptWithActor[InMsg, OutMsg] { request => out =>
    UserSession.props(out, ChatActorSystem.room)
  }

  def chat = Action { implicit request =>
    Ok(views.html.chat())
  }

}