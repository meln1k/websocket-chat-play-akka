import actors.Messages._
import actors.UserSession
import actors.Constants._
import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestActorRef, ImplicitSender}
import org.scalatest._
import scala.concurrent.duration._

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

    "forward user message to the room if name is registered" in {
      val testA = TestActorRef[UserSession](UserSession.props(testActor, testActor))
      val name = util.Random.nextString(10)
      val text = util.Random.nextString(10)
      testA.underlyingActor.userName = Some(name)
      testA ! InMsg(SendMessage, text)
      expectMsg(ChatMessage(name, text))
    }
    "don't send user message to the room if name isn't registered" in {
      val testA = TestActorRef[UserSession](UserSession.props(testActor, testActor))
      val text = util.Random.nextString(10)
      testA.underlyingActor.userName = None
      testA ! InMsg(SendMessage, text)
      expectNoMsg(50 milliseconds)
    }
    "signal when invite denied" in {
      val testA = TestActorRef[UserSession](UserSession.props(testActor, testActor))
      testA ! InviteDenied
      expectMsgPF(hint = """OutMsg(_, "system", "taken")"""){case OutMsg(_, "system", "taken")=>}
    }
    "send chat message to user" in {
      val testA = TestActorRef[UserSession](UserSession.props(testActor, testActor))
      val RandomText = util.Random.nextString(10)
      testA.underlyingActor.userName = Some(RandomText)
      testA ! ChatMessage(RandomText, RandomText)
      expectMsgPF(hint = s"""OutMsg(_, "$RandomText", "$RandomText")"""){case OutMsg(_, RandomText, RandomText)=>}
    }
    "send broadcast message to user" in {
      val testA = TestActorRef[UserSession](UserSession.props(testActor, testActor))
      val RandomText = util.Random.nextString(10)
      testA.underlyingActor.userName = Some(RandomText)
      testA ! Broadcast(RandomText)
      expectMsgPF(hint = s"""OutMsg(_, "toAll", "$RandomText")"""){case OutMsg(_, "toAll", RandomText)=>}
    }
  }

}
