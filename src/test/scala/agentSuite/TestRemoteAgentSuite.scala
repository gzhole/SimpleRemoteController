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
import agent.ExecuteCmd
import agent.cmdResult
import agent.LookupActor
import agent.executionResult
import akka.testkit.TestActorRef
import agent.RemoteMultiLookup
import agent.Utility
import agent.ExecuteRemoteCmd
//import scala.util.Success


/**
 * a Test to show some TestKit examples
 */
@RunWith(classOf[JUnitRunner])
class TestRemoteAgentSuite
  extends TestKit(ActorSystem("TestKitUsageSpec",
    ConfigFactory.parseString(TestKitUsageSpec.config)))
  with DefaultTimeout with ImplicitSender
  with WordSpec with ShouldMatchers with BeforeAndAfterAll {
  import TestKitUsageSpec._
  val remoteActorRef = system.actorOf(Props(new RemoteAgentActor))
 val lookupActor = system.actorOf(Props(new LookupActor))
  override def afterAll {
    system.shutdown()
  }
  "An ExecuteCmd with valid command" should {
    "Respond with the output" in {
      within(500 millis) {
        remoteActorRef ! ExecuteCmd("hostname")
        expectMsg(cmdResult("Gary-ZenBook"))
      }
    }
  }
  
  "An ExecuteCmd with wrong command" should {
    "Respond with the output" in {
      within(1000 millis) {
        remoteActorRef ! ExecuteCmd("hostname1")
        expectMsg(cmdResult("""Cannot run program "hostname1": CreateProcess error=2, The system cannot find the file specified"""))
      }
    }
  }
  
  "An lookup actor call remoteagent actor" should {
    "Respond with the output" in {
      within(1000 millis) {
        lookupActor ! (remoteActorRef, ExecuteCmd("hostname"))
        expectNoMsg
      }
    }
  }
  
  "An lookup actor call remoteagent actor multiple" should {
    "Respond with the output" in {
      within(1000 millis) {
        lookupActor ! (remoteActorRef, ExecuteCmd("hostname"))
        lookupActor ! (remoteActorRef, ExecuteCmd("hostname"))
        expectNoMsg
      }
    }
  }
  
   "An lookup actor call remoteagent actor1" should {
    "Respond with the output" in {
      within(1000 millis) {
        import akka.pattern.ask
        import scala.concurrent.Await
        import scala.concurrent.duration._
        val actorRef = TestActorRef(new LookupActor)
        val future = actorRef ? (cmdResult("result"))
        
        println (future.value)
      //  lookupActor ! (remoteActorRef, ExecuteCmd("hostname"))
      //  expectNoMsg
      }
    }
  }
   
}
object TestRemoteAgentSuite {
  // Define your test specific configuration here
  
  val config = """
akka {
loglevel = "WARNING"
}
"""
}
