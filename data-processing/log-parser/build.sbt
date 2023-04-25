// Versions
val selfVersion = "0.1.0-SNAPSHOT"
val sparkVersion = "3.4.0"
val pureConfigVersion = "0.17.2"
val logbackClassicVersion = "1.4.6"
val scalaLoggingVersion = "3.9.5"
val scalaTestVersion = "3.2.15"
//val antlr4Version = "4.12.0"

lazy val root = (project in file("."))
  .enablePlugins(Antlr4Plugin)
  .settings(
    name := "log-parser",
    ThisBuild / version := selfVersion,
    ThisBuild / scalaVersion := "2.12.12",
    assembly / mainClass := Some("org.tycoon.LogParser"),
    assembly / assemblyJarName := "log-parser.jar",
    Antlr4 / antlr4Version := "4.9.3",
    Antlr4 / antlr4PackageName := Some("org.tycoon.parser.antlr"),
    Antlr4 / antlr4GenListener := false,
    Antlr4 / antlr4GenVisitor := true,
    Antlr4 / antlr4TreatWarningsAsErrors := true,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    ),
  )
