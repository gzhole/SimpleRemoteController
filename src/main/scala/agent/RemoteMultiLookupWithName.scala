
package agent

import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.kernel.Bootable
import akka.util.Timeout

//#imports
class RemoteMultiLookupWithName(remoteIps: Map[String,String]) extends Bootable {

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

  val system = ActorSystem("RemoteLookup", ConfigFactory.load(customConf).getConfig("remotelookup"))
  val actor = system.actorOf(Props[LookupActor], "lookupActor")
  // val commandActor = system.actorFor("akka://RemoteAgent@"+remoteIp+":2552/user/RemoteAgentActor")
  //  commandActor ! ExecuteCmd("""ipconfig.exe""")

  def doSomething( op: ExecutionOp) = {
    import akka.pattern.ask
    import scala.concurrent.duration.FiniteDuration
    import scala.concurrent.duration._
    import scala.concurrent.Future
    import scala.concurrent.Await
    import scala.concurrent._
    import scala.concurrent.util._
    import ExecutionContext.Implicits.global
    import scala.concurrent.duration.Duration

    implicit val timeout = Timeout(20 seconds)
    val remoteClients = for {
      i <- remoteIps.keySet
      val f1 = ask(system.actorFor("akka://RemoteAgent@" + i + ":2550/user/"+remoteIps.getOrElse(i, "RemoteAgentActor")), op).mapTo[cmdResult]
  //  val f1 = ask(system.actorFor("akka://RemoteAgent@" + i + ":2550/user/"+remoteIps.getOrElse(i, "RemoteAgentActor")), (op)).mapTo[cmdResult]
  
    } yield f1
    val resutls = Await.result(Future.sequence(remoteClients), 3 seconds)
    resutls map {
      case cmdResult(str) => println(str);println()
     // case cmdResult(Exception, str) => println(str);println()
    }
    shutdown
    // actor ! (commandActor, op)
  }
  def startup() {
  }

  def shutdown() {
    system.shutdown()
  }
}

//#actor
object RemoteMultiLookupWithName {
  val  propertiesMap = Map(Utility.getCurrentIP->constant.commonActorName, Utility.getCurrentIP->constant.catologActor)
 // val  propertiesMap = Map(Utility.getCurrentIP->"RemoteAgentActor")
 // println(propertiesMap.ge)
  val app = new RemoteMultiLookupWithName(propertiesMap)
  //  val app = new RemoteLookup("9.26.63.168")
  def main(args: Array[String]) {
    //app.doSomething(ExecuteCmd("""hostname"""))
    //   for {
    //    a<- List("127.0.0.1","127.0.0.1")
    // } yield app.remoteIp1(a).doSomething(ExecuteCmd("""hostname"""))*/
    //   app.doSomething(ExecuteCmd("""gradle -p /root/scripts/instance/agent/wxs/CatalogServer/ -PcatalogServerName=cs1 -PcatalogServiceEndPointsStarting=cs1:localhost:6601:6602 -PlistenerHost=localhost -PlistenerPort=2809 start"""))
    app.doSomething(ExecuteRemoteCmd("""hostname"""))
 //   app.doSomething(ExecuteRemoteCmd("""hostname"""))
 //   app.doSomething(ExecuteRemoteCmd("""hostname"""))
 //   app.doSomething(ExecuteRemoteCmd("""hostname"""))
 
    //  val f = Future(app.doSomething(ExecuteCmd("""ipconfig.exe""")))
    //   f onSuccess{case v => println("Success")}
    //   f onComplete{case v => println("Compless")}
    //  Await.result(f,Duration.Inf)
    //   app.shutdown
  }
  def shutdown = app.shutdown
}