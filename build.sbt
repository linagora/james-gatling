name := "james-gatling"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

enablePlugins(GatlingPlugin)

EclipseKeys.withSource := true

libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.4.3"
libraryDependencies += "io.gatling" % "gatling-core" % "2.2.0"
libraryDependencies += "io.gatling" % "gatling-http" % "2.2.0"
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.0"
libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.0"
