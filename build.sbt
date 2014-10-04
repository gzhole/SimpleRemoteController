import AssemblyKeys._ // put this at the top of the file

name := "AcmeAirTypesafe"

version := "1.0"

scalaVersion := "2.10.2"

libraryDependencies ++= List(
"com.typesafe.akka" %% "akka-actor" % "2.2.3",
"com.typesafe.akka" %% "akka-kernel" % "2.2.3",
"com.typesafe.akka" %% "akka-testkit" % "2.2.3",
"com.typesafe.akka" %% "akka-remote" % "2.2.3",
"org.scalatest" % "scalatest_2.10" % "2.0" % "test",
"com.ning" % "async-http-client" % "1.7.19",
"ch.qos.logback" % "logback-classic" % "1.0.7"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

org.scalastyle.sbt.ScalastylePlugin.Settings

assemblySettings

jarName in assembly := "AcmeAirAgent.jar"

test in assembly := {}

mainClass in assembly := Some("agent.RemoteAgentAppFSM")

