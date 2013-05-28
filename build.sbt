import AssemblyKeys._ // put this at the top of the file

name := "SimpleRemoteController"

version := "1.0"

scalaVersion := "2.10.1"

libraryDependencies ++= List(
//"org.scalaz" %% "scalaz-core" % "7.0.0-M9",
"com.typesafe.akka" %% "akka-actor" % "2.1.2",
"com.typesafe.akka" %% "akka-kernel" % "2.1.2",
"com.typesafe.akka" %% "akka-testkit" % "2.1.2",
"com.typesafe.akka" %% "akka-remote" % "2.1.2"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

org.scalastyle.sbt.ScalastylePlugin.Settings

assemblySettings

jarName in assembly := "AcmeAirAgent.jar"

test in assembly := {}

mainClass in assembly := Some("agent.RemoteAgentAppFSM")

