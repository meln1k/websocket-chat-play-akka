package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import actors._


object Application extends Controller {

  def socket = WebSocket.acceptWithActor[String, String] { request => out =>
    UserSession.props(out, ChatActorSystem.room)
  }

  def chat = Action { implicit request =>
    Ok(views.html.chat())
  }

}