lazy val root = (project in file(".")).settings(
  name := "scala-sbt",
  version := "1.0",
  scalaVersion := "2.13.6",
  mainClass in Compile := Some(
    "com.atm.service.streaming.KafkaStreamsAppBootstrap"
  ),
  mainClass in assembly := Some(
    "com.atm.service.streaming.KafkaStreamsAppBootstrap"
  )
)

libraryDependencies += "org.apache.kafka" % "kafka-streams" % "2.8.0"
// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.3"
// https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-scala
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.3"

assemblyJarName in assembly := "atm-1.0.jar"

// META-INF discarding
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x                             => MergeStrategy.first
}
