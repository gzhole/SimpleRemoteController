package agent

import scala.sys.process.Process
case class Executor(str: String = "hostname") {

  val pb = Process(str)
  val os = System.getProperty("os.name")
  val isWindowsOS = os contains ("Windows")
  def getSystemInfo(str: String) = {
    System.getProperty(str)
  }

  def execution = {
    val exitCode = pb.!

  }
  def newProcessExecution = {
    pb.run
  }
  def getOutput = {
    pb.lines_!.toList mkString "\n"
  }
def getOptionOutput= {
  try {
      pb.lines_!.toList mkString "\n"
      //  println(result)
    } catch {
      case e: Exception => e.getMessage()
    }
}

def getOptionOutputWithException= {
  try {
      pb.lines_!.toList mkString "\n"
      //  println(result)
    } catch {
      case e: Exception => ("Exception: " + e.getMessage())
    }
}

def getResultWithException= {
        pb.lines_!.toList mkString "\n"
}
  def processIOExecution = {
    import scala.sys.process.ProcessIO
    val pio = new ProcessIO(_ => (),
      stdout => scala.io.Source.fromInputStream(stdout)
        .getLines.foreach(println),
      _ => ())
    pb.run(pio)

  }
}

