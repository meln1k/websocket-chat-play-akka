name := """websocket-chat-play-akka"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.3.7",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)
