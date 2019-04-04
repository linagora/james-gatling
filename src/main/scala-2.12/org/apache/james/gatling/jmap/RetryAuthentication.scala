package org.apache.james.gatling.jmap

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder
import io.gatling.http.response.Response

object RetryAuthentication {

  def execWithRetryAuthentication(scenario: HttpRequestBuilder, checks: Seq[HttpCheck]): ChainBuilder =
    exec(scenario.check(status.in(200, 401).saveAs("statusCode")).check(checkIfOk(checks): _*))
      .doIfEquals("${statusCode}", 401){
          JmapAuthentication.authentication()
            .exec(scenario.check(checks: _*))
      }

  def checkIfOk(checks: Seq[HttpCheck]): Seq[HttpCheck] =
    checks.map(check => checkIf((r: Response, s: Session) => r.status.code == 200){check})

}
