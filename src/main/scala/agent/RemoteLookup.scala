package agent

import com.typesafe.config.ConfigFactory

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.kernel.Bootable

//#imports
class RemoteLookup( remoteIp:String="127.0.0.1") extends Bootable {
  
   val currentIpaddress = Utility.getCurrentIP
 // import com.typesafe.config.ConfigFactory
  val customConf = ConfigFactory.parseString(s"""
    //#remotelookup
remotelookup {
 // include "common"

  akka {
     actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }

  remote {
      netty.tcp {
      hostname = "$currentIpaddress"
      port = 2553
    }
    netty {
      hostname = "$currentIpaddress"
    
    }
  }
    remote.netty.port = 2553
  }
}
//#remotelookup
      """)
 
  
  val system = ActorSystem("RemoteLookup",ConfigFactory.load(customConf).getConfig("remotelookup"))
  val actor = system.actorOf(Props[LookupActor], "lookupActor")
  val commandActor = system.actorFor("akka://RemoteAgent@"+remoteIp+":2550/user/catologActor")
//  commandActor ! ExecuteCmd("""ipconfig.exe""")
  
  def doSomething(op: ExecutionOp) = {
   actor ! (commandActor, op)
  }
  def startup() {
  }

  def shutdown() {
    system.shutdown()
  }
}
//#actor
class LookupActor extends Actor {
 
  def receive = {
    case (actor: ActorRef, op: ExecutionOp) => actor ! op
    case result: executionResult => result match {
      case cmdResult(str) => {
      // str
        println("return output is: " + str)
        context.stop(self)
        RemoteLookup.shutdown
       // str
      }
      case _ =>  println("nothing is returned")
    }
  }
 
}
//#actor
object RemoteLookup {
  
   val app = new RemoteLookup(Utility.getCurrentIP)
//  val app = new RemoteLookup("9.26.63.168")
  def main(args: Array[String]) {
   //app.doSomething(ExecuteCmd("""hostname"""))
 //   for {
//    a<- List("127.0.0.1","127.0.0.1")
 // } yield app.remoteIp1(a).doSomething(ExecuteCmd("""hostname"""))*/
 //   app.doSomething(ExecuteCmd("""gradle -p /root/scripts/instance/agent/wxs/CatalogServer/ -PcatalogServerName=cs1 -PcatalogServiceEndPointsStarting=cs1:localhost:6601:6602 -PlistenerHost=localhost -PlistenerPort=2809 start"""))
       app.doSomething(ExecuteCmd("""hostname"""))
  //  val f = Future(app.doSomething(ExecuteCmd("""ipconfig.exe""")))
 //   f onSuccess{case v => println("Success")}
 //   f onComplete{case v => println("Compless")}
  //  Await.result(f,Duration.Inf)
 //   app.shutdown
 }
  def shutdown =  app.shutdown
}