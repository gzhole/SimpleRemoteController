package agent
import akka.kernel.Bootable
import akka.actor.{ Props, Actor, ActorSystem, FSM }
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._


class RemoteAgentActorFSM extends Actor with FSM[Int, (String, ExecutionOp)] {
  //1, ready
  //2, running
  //3, failed
  // 
  def runningTimeout = { scala.concurrent.duration.Duration.Inf }
  def runningTimeoutOperation: RemoteAgentActorFSM.this.State = {
    val currState = stay
    //logger.debug("Got running timeout, staying in state => {}", currState)
    currState
  }
  startWith(1, ("", ExecuteRemoteCmd("")))
  when(1) { //ready
    case Event(("start", ExecuteRemoteCmd(str)), _) => {
      println("receiving command to start: " + str)

      try {
        sender ! cmdResult(Executor(str).getResultWithException + "\n(" + this.self.path.name + ": Running State) --" + "Remote IP: " + Utility.getCurrentIP)
        goto(2)
      } catch {
        case e: Exception => {
          sender ! cmdResult(constant.exceptionString+": " + e.getMessage() + "\n(" + this.self.path.name + ": Failed State) --" + "Remote IP: " + Utility.getCurrentIP)
          goto(3)
        }
      }

    }

    case Event(("stop", ExecuteRemoteCmd(str)), _) => {
      println("receiving command to stop when in Ready state: " + str)
      sender ! cmdResult("(" + this.self.path.name + ": is already in Ready State) --" + "Remote IP: " + Utility.getCurrentIP)
      stay
    }
    case Event(("status", _), _) => {
      sender ! cmdResult("(" + this.self.path.name + ": Ready State) --" + " Remote IP: " + Utility.getCurrentIP)
      stay
    }
    case Event(("fail", ExecuteRemoteCmd(str)), _) => {
     sender ! cmdResult("put "+ this.self.path.name + " to Failed State --" + "Remote IP: " + Utility.getCurrentIP)
     goto(3)
    }
  }
  when(2) { //running
    case Event(("stop", ExecuteRemoteCmd(str)), _) => {
      println("receiving command to stop: " + str)
      try {
        val result = Executor(str).getResultWithException
        sender ! cmdResult(result + "\n(" + this.self.path.name + ": Ready State) -- " + "Remote IP: " + Utility.getCurrentIP)
        goto(1)
      } catch {
        case e: Exception => {
          sender ! cmdResult(constant.exceptionString + ": " + e.getMessage() + "\n(" + this.self.path.name + ": Failed State) -- " + "Remote IP: " + Utility.getCurrentIP)
          goto(3)
        }
      }

    }
    case Event(("start", ExecuteRemoteCmd(str)), _) => {
      println("receiving command to start when in Running state: " + str)
      sender ! cmdResult("(" + this.self.path.name + ": is already in Running State) --" + "Remote IP: " + Utility.getCurrentIP)
      stay
    }
    
    case Event(("fail", ExecuteRemoteCmd(str)), _) => {
     sender ! cmdResult("put "+ this.self.path.name + " to Failed State --" + "Remote IP: " + Utility.getCurrentIP)
     goto(3)
    }
    
    case Event(("status", _), _) => {
      sender ! cmdResult("(" + this.self.path.name + ": Running State) --" + " Remote IP: " + Utility.getCurrentIP)
      stay
    }
  }
  when(3) { //failed
    case Event(("start", ExecuteRemoteCmd(str)), _) => {
      try {
        println("receiving command in failed state: " + str)
        sender ! cmdResult(Executor(str).getResultWithException + "\n(" + this.self.path.name + ": Running State) -- " + "Remote IP: " + Utility.getCurrentIP)
        goto(2)
      } catch {
        case e: Exception => {
          sender ! cmdResult(constant.exceptionString + ": " + e.getMessage() + "\n(" + this.self.path.name + ": Failed State) -- " + "Remote IP: " + Utility.getCurrentIP)
          stay
        }
      }
    }

    case Event(("stop", ExecuteRemoteCmd(str)), _) => {
      println("receiving command to stop when in Failed state: " + str)
      sender ! cmdResult("(" + this.self.path.name + ": is moving to Ready State) --" + "Remote IP: " + Utility.getCurrentIP)
      goto(1)
    }
    case Event(("status", _), _) => {
      sender ! cmdResult("(" + this.self.path.name + ": (Failed State) --" + " Remote IP: " + Utility.getCurrentIP)
      stay
    }
  }

  initialize
  whenUnhandled {
    // common code for both states

    case Event(e, s) => {
      println("received even when unhandled")
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
    }
  }

  onTransition {
    case 2 -> 1 =>
      stateData match {
        case (_, ExecuteRemoteCmd(str)) => println("receiving command in onTransition state, will be block for a while " + str)
        // case Todo(ref, queue) => ref ! Batch(queue)
      }
  }
}

class RemoteAgentFSM extends Bootable {

  // println(localIpAddress)
  val currentIpaddress = Utility.getCurrentIP
  //  import com.typesafe.config.ConfigFactory
  val customConf = ConfigFactory.parseString(s"""
      //#remoteAgent
remoteAgent {
  //include "common"
 
  akka {
   actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }

  remote {
    netty {
      hostname = "$currentIpaddress"
     }
  }
    remote.netty.port = 2552
  }
}
//#remoteAgent
      """)
  val system = ActorSystem("RemoteAgentFSM",
    ConfigFactory.load(customConf).getConfig("remoteAgent"))

  //starting up actors 
  val actor = system.actorOf(Props[RemoteAgentActorFSM], constant.commonActorName)
  val catologActor = system.actorOf(Props[RemoteAgentActorFSM], constant.catologActor)
  val containerActor = system.actorOf(Props[RemoteAgentActorFSM], constant.containerActorName)
  val loaderActor = system.actorOf(Props[RemoteAgentActorFSM], constant.loaderActorName)
  val wlpActor = system.actorOf(Props[RemoteAgentActorFSM], constant.wlpActorname)
  val jmeterActor = system.actorOf(Props[RemoteAgentActorFSM], constant.jmeterActorName)
  val nmonActor = system.actorOf(Props[RemoteAgentActorFSM], constant.nmonActor)
  //#setup

  def startup() {
  }

  def shutdown() {
    system.shutdown()
  }

}

object RemoteAgentAppFSM {
  def main(args: Array[String]) {
    new RemoteAgentFSM
    println("Started RemoteAgentFSM Application - waiting for messages")
     new RemoteAgent
     println("Started RemoteAgent for file copy Application - waiting for messages")
  }
}