
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
class RemoteMultiLookupWithNameFSM(remoteIps: Map[String,String]) extends Bootable {

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
  //val actor = system.actorOf(Props[LookupActor], "lookupActor")
  // val commandActor = system.actorFor("akka://RemoteAgent@"+remoteIp+":2552/user/RemoteAgentActor")
  //  commandActor ! ExecuteCmd("""ipconfig.exe""")

  def executeTask(cmd :String, op: ExecutionOp) = {
    import akka.pattern.ask
    import scala.concurrent.duration.FiniteDuration
    import scala.concurrent.duration._
    import scala.concurrent.Future
    import scala.concurrent.Await
    import scala.concurrent._
    import scala.concurrent.util._
    import ExecutionContext.Implicits.global
    import scala.concurrent.duration.Duration

    implicit val timeout = Timeout(300 seconds)
    val remoteClients = for {
      i <- remoteIps.keySet
      val f1 = ask(system.actorFor("akka://RemoteAgentFSM@" + i + ":2552/user/"+remoteIps.getOrElse(i, "RemoteAgentActorFSM")), (cmd,op)).mapTo[cmdResult]
    } yield f1
    val resutls = Await.result(Future.sequence(remoteClients), 300 seconds)
    resutls map {
      case cmdResult(str) => str//);println()
     // case cmdResult(Exception, str) => println(str);println()
    }
   // shutdown
    // actor ! (commandActor, op)
  }
  //String can be "Start" ,"Stop" and "Status".                            //string is ip
   def executeTaskWithProperties(cmd :String, operationsWithProperties: Map[String, ExecutionOp]) = {
    import akka.pattern.ask
    import scala.concurrent.duration.FiniteDuration
    import scala.concurrent.duration._
    import scala.concurrent.Future
    import scala.concurrent.Await
    import scala.concurrent._
    import scala.concurrent.util._
    import ExecutionContext.Implicits.global
    import scala.concurrent.duration.Duration

    implicit val timeout = Timeout(300 seconds)
    val remoteClients = for {
      i <- remoteIps.keySet
        val f1 = ask(system.actorFor("akka://RemoteAgentFSM@" + i + ":2552/user/"+remoteIps.getOrElse(i, "RemoteAgentActorFSM")), (cmd,operationsWithProperties.getOrElse(i, "doing nothing!!!"))).mapTo[cmdResult]
    } yield f1
    val resutls = Await.result(Future.sequence(remoteClients), 300 seconds)
    resutls map {
      case cmdResult(str) =>str//);println()
     // case cmdResult(Exception, str) => println(str);println()
    }
   // shutdown
    // actor ! (commandActor, op)
  }
   
  def executeCopyTask(op: FileData) = {
    import akka.pattern.ask
    import scala.concurrent.duration.FiniteDuration
    import scala.concurrent.duration._
    import scala.concurrent.Future
    import scala.concurrent.Await
    import scala.concurrent._
    import scala.concurrent.util._
    import ExecutionContext.Implicits.global
    import scala.concurrent.duration.Duration

    implicit val timeout = Timeout(300 seconds)
    val remoteClients = for {
      i <- remoteIps.keySet
      val f1 = ask(system.actorFor("akka://RemoteAgent@" + i + ":2550/user/"+remoteIps.getOrElse(i, "RemoteAgentActor")), (op)).mapTo[cmdResult]
    } yield f1
    val resutls = Await.result(Future.sequence(remoteClients), 300 seconds)
    resutls map {
      case cmdResult(str) => str//);println()
     // case cmdResult(Exception, str) => println(str);println()
    }
   // shutdown
    // actor ! (commandActor, op)
  } 
  
  def executeGenaricTask(cmd: ExecuteRemoteCmd) = {
    import akka.pattern.ask
    import scala.concurrent.duration.FiniteDuration
    import scala.concurrent.duration._
    import scala.concurrent.Future
    import scala.concurrent.Await
    import scala.concurrent._
    import scala.concurrent.util._
    import ExecutionContext.Implicits.global
    import scala.concurrent.duration.Duration

    implicit val timeout = Timeout(300 seconds)
    val remoteClients = for {
      i <- remoteIps.keySet
      val f1 = ask(system.actorFor("akka://RemoteAgent@" + i + ":2550/user/"+remoteIps.getOrElse(i, "RemoteAgentActor")), (cmd)).mapTo[cmdResult]
    } yield f1
    val resutls = Await.result(Future.sequence(remoteClients), 300 seconds)
    resutls map {
      case cmdResult(str) => str//);println()
     // case cmdResult(Exception, str) => println(str);println()
    }
   // shutdown
    // actor ! (commandActor, op)
  } 
  def startup() {
  }

  def shutdown() {
    system.shutdown()
    system.awaitTermination
  }
}

//#actor
object RemoteMultiLookupWithNameFSM {
  val  propertiesMap = Map(Utility.getCurrentIP->constant.commonActorName, Utility.getCurrentIP->constant.catologActor)
 // val  propertiesMap = Map(Utility.getCurrentIP->"RemoteAgentActor")
 // println(propertiesMap.ge)
  val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
  //  val app = new RemoteLookup("9.26.63.168")
  def main(args: Array[String]) {
    //app.doSomething(ExecuteCmd("""hostname"""))
    //   for {
    //    a<- List("127.0.0.1","127.0.0.1")
    // } yield app.remoteIp1(a).doSomething(ExecuteCmd("""hostname"""))*/
    //   app.doSomething(ExecuteCmd("""gradle -p /root/scripts/instance/agent/wxs/CatalogServer/ -PcatalogServerName=cs1 -PcatalogServiceEndPointsStarting=cs1:localhost:6601:6602 -PlistenerHost=localhost -PlistenerPort=2809 start"""))
     app.executeTask("status",ExecuteRemoteCmd("""hostname"""))
  //   Thread.sleep(2000)
     
    app.executeTask("start",ExecuteRemoteCmd("""hostname"""))
    app.executeTask("status",ExecuteRemoteCmd("""hostname"""))
     app.executeTask("stop",ExecuteRemoteCmd("""hostname"""))
    app.executeTask("status",ExecuteRemoteCmd("""hostname"""))
        app.shutdown
  }
  def shutdown = app.shutdown
}