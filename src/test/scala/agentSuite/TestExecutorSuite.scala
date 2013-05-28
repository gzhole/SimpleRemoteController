package agentSuite

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import agent.ExecuteCmd
import agent.Executor

@RunWith(classOf[JUnitRunner])
class TestSuite extends FunSuite {

  test("TestRun") {
    assert(ExecuteCmd("str") === new ExecuteCmd("str"))
  }

  test("ExecutorSameProcessSuccess") {
    val result = Executor("""hostname""").getOutput
    //  println(result)
    assert(result === "Gary-ZenBook")
  }

  test("ExecutorSameProcessFail2") {
    try {
      val result = Executor("""hostname1""").getOutput
      fail
      //  println(result)
    } catch {
      case e: Exception => assert(e.getMessage() === """Cannot run program "hostname1": CreateProcess error=2, The system cannot find the file specified""")
    }
  }
  test("ExecutorSameProcessFail1") {
    try {
      val result = Executor("""hostname1""").execution
      fail
      //  println(result)
    } catch {
      case e: Exception => assert(e.getMessage() === """Cannot run program "hostname1": CreateProcess error=2, The system cannot find the file specified""")
    }
  }
  test("ExecutorNewProcessSuccess") {
    val result = Executor("""hostname""").newProcessExecution
    //  println(result)
    assert(result.exitValue == 0)
  }

  test("processIO") {
    val result = Executor("""hostname""").processIOExecution
    //  println(result)
    assert(result.exitValue == 0)
  }

  test("system Properties") {
    val result = Executor().getSystemInfo("os.name")
    println(result)
    assert(result === """Windows 8""")
  }
  
  test("system Properties is Windows") {
    //val result = Executor().isWindowsOS
    //println(result)
    assert( Executor().isWindowsOS)
  }
 test("ExecutorSameProcessSuccess1") {
    val result = Executor("""hostname""").getOptionOutput
    //  println(result)
    assert(result === "Gary-ZenBook")
  }
 
  test("ExecutorSameProcessSuccess2") {
    val result = Executor("""hostname1""").getOptionOutput
    //  println(result)
    assert(result === """Cannot run program "hostname1": CreateProcess error=2, The system cannot find the file specified""")
  }
}
