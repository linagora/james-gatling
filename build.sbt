import sbt.Keys.libraryDependencies

import scala.collection.Seq

lazy val root = (project in file("."))
    .settings(
      name := "james-gatling",
      cancelable in Global := true,
      version := "1.0-SNAPSHOT",
      scalaVersion := "2.13.11",
      libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.1.11" exclude("org.scala-lang.modules", "scala-parser-combinators_2.13"),
      libraryDependencies += "io.gatling" % "gatling-test-framework" % gatlingVersion,
      libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion,
      libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.20" % "test,it",
      libraryDependencies += "com.typesafe.akka" %% "akka-protobuf" % "2.6.20" % "test,it",
      libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.20" % "test,it",
      libraryDependencies += "org.testcontainers" % "testcontainers" % "2.0.2" % "it",

      libraryDependencies += "com.github.azakordonets" %% "fabricator" % "2.1.9",

      // Temporary fix for netty version clashes between gatling and imapnio libs
      libraryDependencies += "io.netty" % "netty-tcnative-boringssl-static" % "2.0.45.Final",

      // Dependencies for local Courier library
      libraryDependencies += "com.sun.mail" % "javax.mail" % "1.6.2",
      libraryDependencies += "javax.activation" % "activation" % "1.1.1",
      libraryDependencies += "org.bouncycastle" % "bcpkix-jdk15on" % "1.60" % Optional,
      libraryDependencies += "org.bouncycastle" % "bcmail-jdk15on" % "1.60" % Optional,

      libraryDependencies += "com.github.javafaker" % "javafaker" % "1.0.2"
    )
  .dependsOn(gatlingImap)

val gatlingVersion = "3.11.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps", "-Ywarn-unused:imports",
      "-Wconf:msg=Auto-application to \\`\\(\\)\\` is deprecated:s")

enablePlugins(GatlingPlugin)

lazy val gatlingImap = ProjectRef(uri("https://github.com/linagora/gatling-imap.git"), "gatling-imap")
