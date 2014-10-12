package agent
import akka.kernel.Bootable
import akka.actor.{ Props, Actor, ActorSystem }
import com.typesafe.config.ConfigFactory
import java.io.PrintWriter
import java.io.File
import java.io.FileOutputStream
import actors.FinderActor
import actors.FinderActor.locate
import actors.FinderActor.Result

class RemoteAgentActor extends Actor {
 def getterProps(): Props = Props(new FinderActor())
 def files = "files1234"
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
    case FileBinaryData(path, content) => {
         println("receiving request to copy file: " + path)
         
     // val writer = new PrintWriter(new File(path))
         val fos = new FileOutputStream(new File(path))

      fos.write(content)
      fos.close()
      sender !  cmdResult("(" + this.self.path.name + ": is finished copying to "+path+") --" + "Remote IP: " + Utility.getCurrentIP)//cmdResult("Done!!!")
    }
    case ExecuteRemoteCmd(str) => {
      println("receiving command: " + str)
      sender ! cmdResult(Executor(str).getOptionOutputWithException + "\nRemote IP: " + Utility.getCurrentIP)
    }
    
    case UpdateBinaryFile(startLocation, libName, binaryFiles) => {
    
     println("receiving request to update jar file: " + libName)
       
     	for {
        	files <- Option(new File(files).listFiles)
        	file <- files /*if file.getName.endsWith(".jpg")*/
        } file.delete()
        
        //write to file
         binaryFiles map (binaryFile => {
        	 				println(binaryFile.fileWithFullPath)
        					 val fos = new FileOutputStream(new File(files , binaryFile.fileWithFullPath));
        					 fos.write(binaryFile.contents)
        					 fos.close()
        				}
        )
      context.actorOf(getterProps()) ! locate(startLocation, libName)
    }
    
    case Result(location) => {
      println("got the location" + location)
    }
    case _ =>
      println("Nothing to do")

  }
}

class RemoteAgent(ip :String ="127.0.0.1") extends Bootable {

  // println(localIpAddress)
 // val currentIpaddress = Utility.getCurrentIP
   val currentIpaddress = if (ip.equalsIgnoreCase("127.0.0.1")) Utility.getCurrentIP  else ip
 
//  import com.typesafe.config.ConfigFactory
  val customConf = ConfigFactory.parseString(s"""
      //#remoteAgent
remoteAgent {
  //include "common"
 
  akka {
          log-dead-letters = 0
  log-dead-letters-during-shutdown = off
  
   actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }

  remote {
    enabled-transports = ["akka.remote.netty.tcp"]  
    netty.tcp {
      hostname = "$currentIpaddress"
      port = 2550
      maximum-frame-size = 12800000b
    }
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