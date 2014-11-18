package actors

import akka.actor._

object ChatActorSystem {

  val chatSystem = ActorSystem("ChatSystem")

  val room = chatSystem.actorOf(Props[Room], "Room")

}
