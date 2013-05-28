package agentSuite

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.util.Random
import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.DefaultTimeout
import akka.testkit.ImplicitSender
import akka.testkit.TestKit
import scala.concurrent.duration._
import agent.RemoteAgentActor
import agent.ExecuteRemoteCmd
import agent.cmdResult
import agent.LookupActor
import agent.executionResult
import akka.testkit.TestActorRef
import agent.RemoteMultiLookup
import agent.Utility
import agent.ExecuteRemoteCmd
import agent.ExecutionOp
import agent.RemoteAgentActorFSM
//import scala.util.Success

/**
 * a Test to show some TestKit examples
 */
@RunWith(classOf[JUnitRunner])
class TestFSMAgentSuite
  extends TestKit(ActorSystem("TestKitUsageSpec",
    ConfigFactory.parseString(TestKitUsageSpec.config)))
  with DefaultTimeout with ImplicitSender
  with WordSpec with ShouldMatchers with BeforeAndAfterAll {
  import TestKitUsageSpec._

  import akka.testkit.TestFSMRef
  import akka.actor.FSM
  import scala.concurrent.duration._

  // val remoteActorRef = system.actorOf(Props(new RemoteAgentActor))
 
 // val fsm = TestFSMRef(new Actor with FSM[Int, (String,ExecutionOp)] {
   val fsm = TestFSMRef(new RemoteAgentActorFSM)
  //1, ready
   //2, running
   //3, failed
   // 
   /* startWith(1, ("",ExecuteRemoteCmd("")))
    when(1) {//ready
      case Event(("start",ExecuteRemoteCmd(str)), _) => goto(2) using ("start",ExecuteRemoteCmd(str))
      case Event(("fail",ExecuteRemoteCmd(str)), _) => goto(3) using ("fail",ExecuteRemoteCmd(str))
    }
    when(2) {//running
      case Event(("stop", ExecuteRemoteCmd(str)),_) => goto(1) using ("stop",ExecuteRemoteCmd(str))
    }
    when(3) {//failed
      case Event(("start", ExecuteRemoteCmd(str)), _) => goto(2) using ("start",ExecuteRemoteCmd(str))
    }
  })*/

  "An lookup actor call remoteagent actor1" should {
    "Respond with the output" in {
      within(1000 millis) {
        import akka.pattern.ask
        import scala.concurrent.Await
        import scala.concurrent.duration._
        assert(fsm.stateData == ("",ExecuteRemoteCmd("")))
        fsm ! ("start",ExecuteRemoteCmd("hostname")) // being a TestActorRef, this runs also on the CallingThreadDispatcher
    //    expectMsg(cmdResult("""Gary-ZenBook Remote IP: """+ Utility.getCurrentIP))
        assert(fsm.stateName == 2)
     //   assert(fsm.stateData == ("start",ExecuteRemoteCmd("hostname")))

      fsm ! ("stop",ExecuteRemoteCmd("hostname"))
        assert(fsm.stateName == 1)
     //   assert(fsm.stateData == ("stop",ExecuteRemoteCmd("hostname")))
        
        fsm.setState(stateName = 1)
        assert(fsm.stateName == 1)

        
//        assert(fsm.timerActive_?("test") == false)
 //       fsm.setTimer("test", 12, 10 millis, true)
  //      assert(fsm.timerActive_?("test") == true)
   //     fsm.cancelTimer("test")
    //    assert(fsm.timerActive_?("test") == false)

        //    println (future.value)
        //  lookupActor ! (remoteActorRef, ExecuteRemoteCmd("hostname"))
        //  expectNoMsg
      }
    }
  }
  
   "An lookup actor call remoteagent failed" should {
    "Respond with the output" in {
      within(1000 millis) {
        import akka.pattern.ask
        import scala.concurrent.Await
        import scala.concurrent.duration._
      //  assert(fsm.stateData == ("",ExecuteRemoteCmd("")))
        fsm ! ("fail",ExecuteRemoteCmd("hostname")) // being a TestActorRef, this runs also on the CallingThreadDispatcher
        assert(fsm.stateName == 3)
     //   assert(fsm.stateData == ("fail",ExecuteRemoteCmd("hostname")))

        fsm ! ("start",ExecuteRemoteCmd("hostname")) // being a TestActorRef, this runs also on the CallingThreadDispatcher
        assert(fsm.stateName == 2)
     //   assert(fsm.stateData == ("start",ExecuteRemoteCmd("hostname")))
        
        fsm ! ("stop",ExecuteRemoteCmd("hostname"))
        assert(fsm.stateName == 1)
     //   assert(fsm.stateData == ("stop",ExecuteRemoteCmd("hostname")))
     //   fsm ! ("stop",ExecuteRemoteCmd("catalogStop"))
      //  assert(fsm.stateName == 1)
      //  assert(fsm.stateData == ("stop",ExecuteRemoteCmd("catalogStop")))
        
        
        
       

        //    println (future.value)
        //  lookupActor ! (remoteActorRef, ExecuteRemoteCmd("hostname"))
        //  expectNoMsg
      }
    }
  }
   
    "An lookup actor call remoteagent exceptions" should {
    "Respond with the output1" in {
      within(1000 millis) {
      //  import akka.pattern.ask
      //  import scala.concurrent.Await
      //  import scala.concurrent.duration._
       fsm.setState(stateName = 1)
        assert(fsm.stateName == 1)
      fsm ! ("start",ExecuteRemoteCmd("hostname1")) // being a TestActorRef, this runs also on the CallingThreadDispatcher
    //   expectMsg(cmdResult("""Exception: Cannot run program "hostname1": CreateProcess error=2, The system cannot find the file specified Remote IP: """+ Utility.getCurrentIP))
      }
    }
  } 
  //assert(fsm.stateName == 1)

}
object TestRemoteFSMAgentSuite {
  // Define your test specific configuration here

  val config = """
akka {
loglevel = "WARNING"
}
"""
}
