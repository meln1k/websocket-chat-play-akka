import actors.Messages._
import actors.Room
import akka.testkit.TestProbe
import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestActorRef, ImplicitSender}
import org.scalatest._
import scala.concurrent.duration._

class RoomSpec extends TestKit(ActorSystem("RoomSpec"))
                with WordSpecLike
                with MustMatchers
                with BeforeAndAfterAll
                with ParallelTestExecution
                with ImplicitSender {

  override def afterAll() {system.shutdown()}

  "Room" should {
    "subscribe user if invite requested" in {
      val testA = TestActorRef[Room]
      val name = util.Random.nextString(10)
      testA ! InviteRequest(name)
      testA.underlyingActor.sessions.keySet must contain (name)
    }

    "deny subscription if name already exists" in {
      val testA = TestActorRef[Room]
      val name = util.Random.nextString(10)
      testA.underlyingActor.sessions += name -> testActor
      testA ! InviteRequest(name)
      expectMsg(InviteDenied)
    }

    "send message to all subscribers" in {
      val probes = 1 to 5 map (_ => TestProbe())
      val subscribers = probes.map(p => util.Random.nextString(10) -> p.ref)
      val testA = TestActorRef[Room]
      val text = util.Random.nextString(10)
      testA.underlyingActor.sessions ++= subscribers
      testA ! ChatMessage("foo", text)
      probes.foreach(_.expectMsg(500 millis, ChatMessage("foo", text)))
    }

    "unsubscribe user" in {
      val testA = TestActorRef[Room]
      val name = util.Random.nextString(10)
      testA.underlyingActor.sessions += name -> testActor
      testA ! Unsubscribe(name)
      testA.underlyingActor.sessions.size must be (0)
    }
  }
}
