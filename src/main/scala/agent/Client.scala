package agent

import scala.io.Source

trait Client {
 
  def process(step: scala.collection.immutable.Set[String], failable: Boolean = true, p: String => Boolean = _ => false) = {
    step map println
    if (!failable && ((step exists (x => x.substring(0, 9) == "Exception")) || (step exists (p)))) {
      failActor(step)
      throw new RuntimeException("found exceptions, will stop the rest of the steps")
    }
  }

  def failActor(results: scala.collection.immutable.Set[String]) = {
    for {
      result <- results
      if (result.indexOf(constant.runningState) > 0)
    } yield callActor(result) map println

  }

  def callActor(str: String) = {
    val start = str.indexOf('(')
    val stop = str.indexOf(": Running State")
    val actorName = str.substring(start + 1, stop)

    val startIp = str.indexOf("Remote IP: ")
    val ip = str.substring(startIp + 11, str.length())
    val propertiesMap = Map(ip -> actorName)
    step(propertiesMap, "fail", "")
  }

  def step(propertiesMap: Map[String, String], op: String, cmd: String) = {
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
    val result = app.executeTask("fail", ExecuteRemoteCmd(cmd))
    app.shutdown
    result
  }

  def stepWithProperties(propertiesMap: Map[String, String], op: String, cmdProperties: Map[String, ExecuteRemoteCmd]) = {
    val app = new RemoteMultiLookupWithNameFSM(propertiesMap)
    val result = app.executeTaskWithProperties(op, cmdProperties)
    app.shutdown
    result
  }
  

}

