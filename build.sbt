name := "james-gatling"
cancelable in Global := true

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.8"

val gatlingVersion = "3.0.3"
val playWsVersion = "2.0.1"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps")

enablePlugins(GatlingPlugin)

resolvers += "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven"
resolvers += "Fabricator" at "http://dl.bintray.com/biercoff/Fabricator"

libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % playWsVersion
libraryDependencies += "io.gatling" % "gatling-test-framework" % gatlingVersion
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion

libraryDependencies += "com.github.azakordonets" %% "fabricator" % "2.1.5"

// Dependencies for local Courier library
libraryDependencies += "com.sun.mail" % "javax.mail" % "1.6.2"
libraryDependencies += "javax.activation" % "activation" % "1.1.1"
libraryDependencies += "org.bouncycastle" % "bcpkix-jdk15on" % "1.60" % Optional
libraryDependencies += "org.bouncycastle" % "bcmail-jdk15on" % "1.60" % Optional
