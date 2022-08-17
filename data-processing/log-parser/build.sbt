ThisBuild / name := "log-parser"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"

// Versions
val sparkVersion = "3.3.0"
val hadoopAwsVersion = "3.3.2"
val awsJavaSdkVersion = "1.11.1026"
val pureConfigVersion = "0.17.1"
val logbackClassicVersion = "1.2.11"
val scalaLoggingVersion = "3.9.5"

// Dependencies
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % hadoopAwsVersion % "provided"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-bundle" % awsJavaSdkVersion % "provided"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
libraryDependencies += "ch.qos.logback" % "logback-classic" % logbackClassicVersion
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % Test


lazy val root = (project in file("."))
  .settings(
    name := "log-parser"
  )
