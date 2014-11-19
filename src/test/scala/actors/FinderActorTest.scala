package actors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestProbe
import scala.concurrent.duration._
import akka.testkit.TestKit
import akka.testkit.ImplicitSender
import actors.FinderActor.locate
import actors.FinderActor.Result



object FinderActorTest extends App {
 implicit val system = ActorSystem("TestSys")
  val actor = system.actorOf(Props[FinderActor])
 
  try {
  // running a TestProbe from the outside
  val p = TestProbe()
  p.send(actor, locate(".", "Getter.scala"))
  var result = Result(Set(""".\src\main\scala\info\rkuhn\linkchecker\Getter.scala"""))
 // result. = ""
  p.expectMsg(10.second, result)
  } finally {
  system.shutdown()

  }
  println("done")
  
  
}