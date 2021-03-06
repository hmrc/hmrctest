/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.it.servicemanager

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ahc.AhcWSClient
import uk.gov.hmrc.play.it.{ExternalService, TestId}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.sys.addShutdownHook


object ServiceManagerClient {

  implicit val system = ActorSystem("serviceManagerClient")
  addShutdownHook(system.terminate)
  implicit val mat = ActorMaterializer()

  protected val serviceManagerStartUrl           = "http://localhost:8085/start"
  protected val serviceManagerStopUrl            = "http://localhost:8085/stop"
  private   val serviceManagerVersionVariableUrl = "http://localhost:8085/version_variable"
  implicit  val externalServiceFormat            = Json.format[ExternalService]
  implicit  val startRequestFormat               = Json.format[ServiceManagementStartRequest]
  implicit  val stopRequestFormat                = Json.format[ServiceManagementStopRequest]
  implicit  val responseFormat                   = Json.format[ServiceManagementResponse]
  implicit  val versionEnvironmentVariableFormat = Json.format[VersionEnvironmentVariable]
  lazy      val client                           = new AhcWSClient(new DefaultAsyncHttpClientConfig.Builder().build)

  def start(testId: TestId, externalServices: Seq[ExternalService], timeout: Duration): Map[String, Int] = {

    if (externalServices.isEmpty)
      Map.empty
    else {
      val extendedTimeoutClient = new AhcWSClient(
        {
          val builder = new DefaultAsyncHttpClientConfig.Builder()
          builder.setPooledConnectionIdleTimeout(timeout.toMillis.toInt).setReadTimeout(timeout.toMillis.toInt)
          builder.build()
        })

      val f = extendedTimeoutClient.url(serviceManagerStartUrl)
        .withRequestTimeout(timeout)
        .post(Json.toJson(ServiceManagementStartRequest(testId.toString, externalServices)))
        .map { response: WSResponse =>

          if (response.status >= 200 && response.status <= 299) {
            val servicePorts: Seq[ServiceManagementResponse] = response.json.validate[Seq[ServiceManagementResponse]].fold(
              errs => throw new JsException("POST", serviceManagerStartUrl, response.body, classOf[Seq[ServiceManagementResponse]], errs),
              valid => valid)

            servicePorts.map(s => s.serviceName -> s.port).toMap
          } else {
            throw new RuntimeException(s"Received unexpected response from ServiceManager: ${response.status}\n\n" + response.body)
          }
        }

      Await.result(f.andThen { case _ => extendedTimeoutClient.close() }, 5.minutes)
    }
  }

  def stop(testId: TestId, dropDatabases: Boolean) {
    Await.result(
      client.url(serviceManagerStopUrl)
        .post(Json.toJson(ServiceManagementStopRequest(testId.toString, dropDatabases))), 30.seconds)
  }

  def version_variable(service: String): VersionEnvironmentVariable = {
    val versionEnvironmentVariable: Future[VersionEnvironmentVariable] = client.url(serviceManagerVersionVariableUrl).withQueryString("service" -> service).get().map {
      response: WSResponse =>
        response.json.validate[VersionEnvironmentVariable].fold(
          errors => throw new JsException("GET", s"$serviceManagerVersionVariableUrl?service=$service", response.body, classOf[VersionEnvironmentVariable], errors),
          valid => valid
        )
    }

    Await.result(versionEnvironmentVariable, 5.minutes)
  }
}

case class VersionEnvironmentVariable(variable: String)

case class ServiceManagementStartRequest(testId: String, services: Seq[ExternalService])

case class ServiceManagementStopRequest(testId: String, dropDatabases: Boolean)

case class ServiceManagementResponse(port: Int, serviceName: String)
