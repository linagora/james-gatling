package org.apache.james.gatling.simulation

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

import scala.util.Properties

object GatlingRunnerSample extends App {
  val simulationClass: String = Properties.envOrElse("SIMULATION_CLASS", "org.apache.james.gatling.simulation.jmap.rfc8621.PushPlatformValidationSimulation")
  val props: GatlingPropertiesBuilder = new GatlingPropertiesBuilder
  props.resourcesDirectory("src/main/scala-2.12")
  props.binariesDirectory("target/scala-2.12/classes")
  props.resultsDirectory("/tmp/gatling-result")
  props.simulationClass(simulationClass)

  Gatling.fromMap(props.build)
}



