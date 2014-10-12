package agent

sealed trait ExecutionOp
case class ExecuteCmd(str: String) extends ExecutionOp

case class FileData(fileWithFullPath: String, contents :String) extends ExecutionOp
case class FileBinaryData(fileWithFullPath: String, contents :Array[Byte]) extends ExecutionOp
case class UpdateBinaryFile(startLocation: String, libName: String,binaryFiles: Set [FileBinaryData]) extends ExecutionOp

//this one return ip address
case class ExecuteRemoteCmd(str: String) extends ExecutionOp

sealed trait executionResult
case class cmdResult(output: String) extends executionResult
case class RunTaskResult(val failure: Option[String], val output: String) extends executionResult
object constant {
  val commonActorName = """CommonActor"""
  val catologActor = """CatologActor"""
  val containerActorName = """ContainerActor"""
  val wlpActorname = """WLPActor"""
  val loaderActorName ="""LoaderActor"""  
  val jmeterActorName = """JMeterActor"""
  val nmonActor = """NmonActor"""
    val exceptionString = "Exception"
  val runningState =     """Running State"""
}
