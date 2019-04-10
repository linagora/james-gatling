package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.concurrent.duration._

object JmapAuthentication {

  def obtainContinuationToken() =
    exec(
      http("obtainContinuationToken")
        .post("/authentication")
        .headers(Map("Content-Type"-> "application/json; charset=UTF-8", "Accept"->"application/json"))
        .body(StringBody("""{"username": "${username}",
          "clientName": "Mozilla Thunderbird",
          "clientVersion": "42.0",
          "deviceName": "Joe Blogg’s iPhone"}"""))
        .check(status.is(200))
        .check(jsonPath("$.continuationToken").saveAs("continuationToken")))

  def obtainAccessToken() =
    exec(
      http("obtainAccessToken")
        .post("/authentication")
        .headers(Map("Content-Type"-> "application/json; charset=UTF-8", "Accept"->"application/json"))
        .body(StringBody("""{"token": "${continuationToken}",
          "method": "password",
          "password": "${password}"}"""))
        .check(status.is(201))
        .check(jsonPath("$.accessToken").saveAs("accessToken"))
        .check(jsonPath("$.api").saveAs("api"))
        .check(jsonPath("$.eventSource").saveAs("eventSource"))
        .check(jsonPath("$.upload").saveAs("upload"))
        .check(jsonPath("$.download").saveAs("download")))

  def authentication() =
    obtainContinuationToken()
      .pause(1 second)
      .exec(obtainAccessToken())

  def authenticatedQuery(requestName: String, endPoint: String): HttpRequestBuilder =
    http(requestName)
      .post(endPoint)
      .header("Authorization", "${accessToken}")
      .header("Accept", "application/json")

}
