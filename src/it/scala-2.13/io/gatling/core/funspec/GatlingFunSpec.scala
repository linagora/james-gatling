/*
 * Copyright 2011-2024 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// ponytail: Gatling dropped `io.gatling.core.funspec.GatlingFunSpec` after 3.12.
// Our IT specs rely on it for the spec/before/after pattern. Adapted to gatling 3.15,
// where Simulation.params became parameterless and ScenarioBuilder/ChainBuilder.actionBuilders
// became private[core]: the whole scenario (an Executable) is registered as a single spec.
package io.gatling.core.funspec

import scala.collection.mutable.ListBuffer

import io.gatling.core.Predef._
import io.gatling.core.action.builder.Executable
import io.gatling.core.protocol.Protocol
import io.gatling.core.scenario.SimulationParams

abstract class GatlingFunSpec extends Simulation {

  def protocolConf: Protocol

  def spec(executable: Executable): ListBuffer[Executable] = specs += executable

  private[this] val specs = new ListBuffer[Executable]

  private[this] lazy val testScenario = scenario(this.getClass.getSimpleName)
    .exec(specs.toList)

  private def setupRegisteredSpecs(): Unit = {
    require(specs.nonEmpty, "At least one spec needs to be defined")
    setUp(testScenario.inject(atOnceUsers(1)))
      .protocols(protocolConf)
      .assertions(forAll.failedRequests.percent.is(0))
  }

  override private[gatling] def params: SimulationParams = {
    setupRegisteredSpecs()
    super.params
  }
}
