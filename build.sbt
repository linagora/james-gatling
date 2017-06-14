name := "james-gatling"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

enablePlugins(GatlingPlugin)

EclipseKeys.withSource := true

resolvers += "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven"

libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.4.3"
libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.2"
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2"

libraryDependencies += "ch.lightshed" %% "courier" % "0.1.4"