package agent

object Utility {
  def getCurrentIP = {
    import java.net._
    InetAddress.getLocalHost.getHostAddress()
  }
   def predicate(string: String) = string.contains("BUILD FAILED")
}