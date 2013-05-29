package agent
import akka.kernel.Bootable
import akka.actor.{ Props, Actor, ActorSystem }
import com.typesafe.config.ConfigFactory
import java.io.PrintWriter
import java.io.File

class RemoteAgentActor extends Actor {

  def receive = {
    case ExecuteCmd(str) => {
      println("receiving command: " + str)
      sender ! cmdResult(Executor(str).getOptionOutput)
    }
    case FileData(path, content) => {
         println("receiving request to copy file: " + path)
      val writer = new PrintWriter(new File(path))
      writer.write(content)
      writer.close()
      sender !  cmdResult("(" + this.self.path.name + ": is finished copying to "+path+") --" + "Remote IP: " + Utility.getCurrentIP)//cmdResult("Done!!!")
    }
    /* case TickDelete => {
      println("callled")
      println(Executor("""c:\taskDelete.bat""").getOutput)
    }
    case TickShowInfo => {
      println("receiving command: show info" )
      sender ! cmdResult(Executor("""c:\task.bat""").getOutput)
    } 
     case Tick(str) => {
      println("receiving Tick command: " + str)
      sender ! cmdResult(Executor(str).getOutput)
     // println(Executor("""c:\taskDelete.bat""").getOutput)
    }*/
    case ExecuteRemoteCmd(str) => {
      println("receiving command: " + str)
      sender ! cmdResult(Executor(str).getOptionOutputWithException + "\nRemote IP: " + Utility.getCurrentIP)
    }
    case _ =>
      println("Nothing to do")

  }
}

class RemoteAgent extends Bootable {

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
    remote.netty.port = 2550
  }
}
//#remoteAgent
      """)
  val system = ActorSystem("RemoteAgent",
    ConfigFactory.load(customConf).getConfig("remoteAgent"))
  val actor = system.actorOf(Props[RemoteAgentActor], constant.commonActorName)
//   val catologActor = system.actorOf(Props[RemoteAgentActor], constant.catologActor)
  //#setup

  def startup() {
  }

  def shutdown() {
    system.shutdown()
  }


}

object RemoteAgentApp {
  def main(args: Array[String]) {
    new RemoteAgent
    println("Started RemoteAgent Application with 2550 - waiting for messages")
  }
}