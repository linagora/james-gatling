import sbt.Keys.libraryDependencies

lazy val root = (project in file("."))
    .settings(
      name := "james-gatling",
      cancelable in Global := true,
      version := "1.0-SNAPSHOT",
      scalaVersion := "2.12.8",
      libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % playWsVersion,
      libraryDependencies += "io.gatling" % "gatling-test-framework" % gatlingVersion,
      libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion,
      libraryDependencies += "org.testcontainers" % "testcontainers" % "1.11.0" % "it",

      libraryDependencies += "com.github.azakordonets" %% "fabricator" % "2.1.5",

      // Temporary fix for netty version clashes between gatling and imapnio libs
      libraryDependencies += "io.netty" % "netty-tcnative-boringssl-static" % "2.0.45.Final",

      // Dependencies for local Courier library
      libraryDependencies += "com.sun.mail" % "javax.mail" % "1.6.2",
      libraryDependencies += "javax.activation" % "activation" % "1.1.1",
      libraryDependencies += "org.bouncycastle" % "bcpkix-jdk15on" % "1.60" % Optional,
      libraryDependencies += "org.bouncycastle" % "bcmail-jdk15on" % "1.60" % Optional
    )
  .dependsOn(gatlingImap)

// TODO enable WebsocketSimpleScenario and PushPlatformValidationScenario' websocketclose request when upgrade gatlingVersion
val gatlingVersion = "3.0.3"
val playWsVersion = "2.0.1"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps", "-Ywarn-unused:imports")

enablePlugins(GatlingPlugin)

lazy val gatlingImap = ProjectRef(uri("https://github.com/chibenwa/gatling-imap.git"), "gatling-imap")
