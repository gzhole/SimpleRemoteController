package agentSuite

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import agent.ExecuteCmd
import agent.Executor
import agent.Utility
import agent.constant
import agent.RemoteMultiLookupWithNameFSM
import agent.ExecuteRemoteCmd

@RunWith(classOf[JUnitRunner])
class TestRemoteLookupWithFSM extends FunSuite {
  test("TestRun") {
    assert(ExecuteCmd("str") === new ExecuteCmd("str"))
  }
//commnet out for now, need to start RemoteAgentFSM first
 /* test("lookup with multile teardown") {
	  newSystemrotine
	  newSystemrotine
	  newSystemrotine
	   newSystemrotine
	    newSystemrotine
   
  }*/
  
  def newSystemrotine = {
    
    val propertiesMap = Map(Utility.getCurrentIP -> constant.commonActorName)
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
    //  val app = new RemoteLookup("9.26.63.168")
    app.executeTask("status", ExecuteRemoteCmd("""hostname"""))
     app.executeTask("start", ExecuteRemoteCmd("""hostname"""))
     app.shutdown
  }
}