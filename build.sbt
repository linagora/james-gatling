name := "james-gatling"
cancelable in Global := true

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

enablePlugins(GatlingPlugin)

EclipseKeys.withSource := true

resolvers += "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven"
resolvers += "Fabricator" at "http://dl.bintray.com/biercoff/Fabricator"

libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.4.3"
libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.2"
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2"

libraryDependencies += "com.github.azakordonets" %% "fabricator" % "2.1.5"

// Dependencies for local Courier library
libraryDependencies += "com.sun.mail"      % "javax.mail"      % "1.6.2"
libraryDependencies += "javax.activation"  % "activation"      % "1.1.1"
libraryDependencies += "org.bouncycastle"  % "bcpkix-jdk15on"  % "1.60" % Optional
libraryDependencies += "org.bouncycastle"  % "bcmail-jdk15on"  % "1.60" % Optional
