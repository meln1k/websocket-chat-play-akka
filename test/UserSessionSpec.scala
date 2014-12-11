import actors.Messages._
import actors.UserSession
import actors.Constants._
import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.testkit.{TestKit, TestActorRef, ImplicitSender}
import org.scalatest._

class UserSessionSpec extends TestKit(ActorSystem("UserSessionSpec"))
                          with WordSpecLike
                          with MustMatchers
                          with BeforeAndAfterAll
                          with ParallelTestExecution {

  override def afterAll() {system.shutdown()}

  "UserSession" should {
    "set the name when invite request accepted" in {
      val name = util.Random.nextString(10)
      val real = TestActorRef[UserSession](UserSession.props(testActor, testActor)).underlyingActor
      real.receive(InviteAccepted(name))
      real.userName must be (Some(name))
    }

    "send message to the room" in {
      val testA = TestActorRef[UserSession](UserSession.props(testActor, testActor))
      val name = util.Random.nextString(10)
      val text = util.Random.nextString(10)
      testA.underlyingActor.userName = Some(name)
      testA ! InMsg(SendMessage, text)
      expectMsg(ChatMessage(name, text))
    }
  }

}
