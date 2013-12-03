

import agent.RemoteMultiLookupWithNameFSM
import agent.Utility
import agent.constant
import agent.ExecuteRemoteCmd
import agent.FileData

object Client {

  def main(args: Array[String]): Unit = {
    args match {
       case Array(command) => execute(command)
       case Array(command, ip) => execute(command, ip)
       case Array("copy", command, ip) => executeCopy(command, ip)
       case _ => usage
    
      
    }
    
  }

  def usage = {
    println(
        """This is a client wrapper to invoke command on remove machine.
Run command on local machine: Java Client COMMAND. E.g. Java Client hostname
Run command on remote machine: Java Client COMMAND IP. E.g. Java Client hostname 192.168.0.1
    """)
  }
  
  def execute(cmd :String) = {
    val propertiesMap = Map(Utility.getCurrentIP -> constant.commonActorName)
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
    val result:Set[String] = app.executeGenaricTask(new ExecuteRemoteCmd(cmd))
    app.shutdown
    result foreach(x=> println(x))
  
  }
  
  def execute(cmd :String, ip:String) = {
    val propertiesMap = Map(ip -> constant.commonActorName)
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
    val result:Set[String] = app.executeGenaricTask(new ExecuteRemoteCmd(cmd))
    app.shutdown
    result foreach(x=> println(x))
  }
  
   def executeCopy(fileNameWithFullPath :String, ip:String) = {
    val propertiesMap = Map(ip -> constant.commonActorName)
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
    import scala.io.Source
    val contents = Source.fromFile(fileNameWithFullPath).mkString
  
   val result = app.executeCopyTask(new FileData(fileNameWithFullPath, contents))
   app.shutdown
    result foreach(x=> println(x))
  }
}