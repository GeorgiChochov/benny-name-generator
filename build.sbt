name := "akka-http-test"

version := "0.1"

scalaVersion := "2.13.5"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
)
enablePlugins(JavaAppPackaging)
mainClass in Compile := Some("io.github.georgichochov.server.Main")
