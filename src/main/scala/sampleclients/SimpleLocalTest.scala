package sampleclients

import agent.Client
import agent.ExecuteRemoteCmd
import agent.RemoteAgent
import agent.RemoteMultiLookupWithNameFSM
import agent.Utility
import agent.constant
import agent.FileData
import scala.io.Source

object SimpleLocalTest extends Client {

  def main(args: Array[String]): Unit = {

    process(testCmd1)
    process(testCmdWithMultiRemoteHosts)
    process(testCmdCopyTxtFileToRemote)

  }

  def testCmd1 = {
    val agent = new RemoteAgent
    Thread.sleep(3000)
    val propertiesMap = Map(Utility.getCurrentIP -> constant.commonActorName)
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
    val result = app.executeGenaricTask(new ExecuteRemoteCmd("hostname"))
    app.shutdown
    agent.shutdown
    result
  }
  
  def testCmdWithMultiRemoteHosts = {
    val agent = new RemoteAgent
    Thread.sleep(3000)
    val propertiesMap = Map(Utility.getCurrentIP -> constant.commonActorName,Utility.getCurrentIP -> constant.commonActorName)
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
    val result = app.executeGenaricTask(new ExecuteRemoteCmd("hostname"))
    app.shutdown
    agent.shutdown
    result
  }
  
   def testCmdCopyTxtFileToRemote = {
      val agent = new RemoteAgent
    Thread.sleep(3000)
    val propertiesMap = Map(Utility.getCurrentIP -> constant.commonActorName)
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
    val contents =  Source.fromFile("build.xml").mkString
   val result = app.executeCopyTask(new FileData("build1.xml", contents))
  
   app.shutdown
    agent.shutdown
    result
  }

}