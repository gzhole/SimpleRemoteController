package actors

import java.util.ArrayList
import akka.actor.Actor
import akka.actor.Props
import akka.actor.Terminated
import akka.actor.SupervisorStrategy
import akka.actor.ActorLogging
import akka.actor.ReceiveTimeout
import scala.concurrent.duration._
import akka.actor.ActorRef

object FinderActor {
  case class locate(path: String, fileName: String)
  case class Result(links: Set[String])
}

class FinderActor extends Actor with ActorLogging {
  import FinderActor._
  
  context.setReceiveTimeout(90.seconds)
  
 // findFile(".", "Getter.scala") foreach(x=> println(x))
  def receive = {
    case locate(startPath, fileName) =>
      log.debug("{} locateing {}", startPath, " filename: " + fileName)
    //  context.parent ! Result(findFile(startPath, fileName))
      sender ! Result(findFile(startPath, fileName))
  }
  
  def findFile(startPath: String, fileName: String) :Set[String]= {
   val files =  utils.FileUtils.recursiveListFiles(new java.io.File(startPath)).filter(x=>x.getName().endsWith(fileName))
  val locations = files map (x => x.getPath())
   locations.toSet
  // if (locations.length >0 )locations.apply(0) else ""
   //null
  }
}