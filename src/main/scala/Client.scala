

import agent.RemoteMultiLookupWithNameFSM
import agent.Utility
import agent.constant
import agent.ExecuteRemoteCmd
import agent.FileData
import agent.FileBinaryData
import utils.SmallBinaryFiles
import java.io.File
import agent.UpdateBinaryFile

object Client {

  def main(args: Array[String]): Unit = {
    args match {
       case Array(command) => execute(command)
       case Array(command, ip) => execute(command, ip)
       case Array("copy", command, ip) => executeCopy(command, ip)
       case Array("bcopy", command, ip) => executeBinaryCopy(command, ip)
       case _ => /*usage*/ executeUpdateClassFiles(".", "test.jar", "org", "192.168.1.14")
    
      
    }
    
  }

  def usage = {
    println(
        """This is a client wrapper to invoke command on remote machine.
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
import utils.SmallBinaryFiles;
    val contents = Source.fromFile(fileNameWithFullPath).mkString
  
   val result = app.executeCopyTask(new FileData(fileNameWithFullPath, contents))
   app.shutdown
    result foreach(x=> println(x))
  }
   
   def executeBinaryCopy(fileNameWithFullPath :String, ip:String) = {
    val propertiesMap = Map(ip -> constant.commonActorName)
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
   // import scala.io.Source
    val contenttmp = utils.SmallBinaryFiles.readSmallBinaryFile(fileNameWithFullPath)
  
    //  var array1 :Array[Char]= new Array[Char](contenttmp.length)
   //val contents = contenttmp.map(_.toByte).toArray
   val result = app.executeCopyBinaryTask(new FileBinaryData(fileNameWithFullPath, contenttmp))
   app.shutdown
    result foreach(x=> println(x))
  }
   
   def executeUpdateClassFiles(startLocation: String, libName: String, directory :String, ip:String) = {
    val propertiesMap = Map(ip -> constant.commonActorName)
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
   // import scala.io.Source
    val clientfiles = ((new File(directory).listFiles).toSet[File] map (x=>(x.getPath(),x.getName()))) map (y=>new FileBinaryData(y._2, utils.SmallBinaryFiles.readSmallBinaryFile(y._1)))
    
   /* val file1s = for {
        	//files <- Set()
        	file <- clientfiles 
        } yield new FileBinaryData(".", utils.SmallBinaryFiles.readSmallBinaryFile(file))*/
   // val contenttmp = 
  
    //  var array1 :Array[Char]= new Array[Char](contenttmp.length)
   //val contents = contenttmp.map(_.toByte).toArray
   val result = app.executeuUpdateClassFilesTask(new UpdateBinaryFile(startLocation, libName, clientfiles))
   app.shutdown
    result foreach(x=> println(x))
  }
}