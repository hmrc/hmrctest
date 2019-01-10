/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.play.it

import java.io.File

import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTimeUtils, DateTimeZone}
import play.api
import play.api._
import play.core.server.{NettyServer, ServerConfig}
import uk.gov.hmrc.play.it.servicemanager.ServiceManagerClient

import scala.concurrent.duration._
import scala.sys.addShutdownHook

trait ExternalServicesServer extends IntegrationTestConfiguration with ExternalServiceOrchestrator

trait MicroServiceEmbeddedServer extends ExternalServicesServer with EmbeddedServiceOrchestrator

trait MongoMicroServiceEmbeddedServer extends MicroServiceEmbeddedServer with MongoTestConfiguration

trait IntegrationTestConfiguration {

  protected val testName        : String
  protected val externalServices: Seq[ExternalService]
  protected val dropDatabasesAfterTest: Boolean = true

  protected def additionalConfig: Map[String, _ <: Any] = Map.empty

  protected val testId = TestId(testName)
  protected def applicationMode = Mode.Dev
}

trait ExternalServiceOrchestrator extends StartAndStopServer {

  self: IntegrationTestConfiguration =>

  import uk.gov.hmrc.play.it.SafelyStop._
  import uk.gov.hmrc.play.it.UrlHelper._

  protected def startTimeout: Duration = 60.seconds

  protected lazy val externalServicePorts = ServiceManagerClient.start(testId, externalServices, startTimeout)

  override def start() {
    externalServicePorts
  }

  override def stop() {
    safelyStop("stopping external services")(ServiceManagerClient.stop(testId, dropDatabasesAfterTest))
  }

  def externalResource(serviceName: String, path: String): String = {
    val port = externalServicePorts.getOrElse(serviceName, throw new IllegalArgumentException(s"Unknown service '$serviceName'"))
    s"http://localhost:$port/${-/(path)}"
  }
}

trait EmbeddedServiceOrchestrator extends ResourceProvider with StartAndStopServer {

  self: IntegrationTestConfiguration with ExternalServiceOrchestrator =>

  import uk.gov.hmrc.play.it.SafelyStop._
  import uk.gov.hmrc.play.it.UrlHelper._

  protected val servicePort: Int = Port.randomAvailable

  private lazy val server = {

    val configMap = externalServicePorts.foldLeft(Map.empty[String, Any])(
      (map, servicePort) => {
        val serviceName = servicePort._1
        val port = servicePort._2
        Logger.debug(s"External service '$serviceName' is running on port: $port")

        val updatedMap = map +
          (s"$applicationMode.microservice.services.$serviceName.port" -> new Integer(port)) +
          (s"$applicationMode.microservice.services.$serviceName.host" -> "localhost")

        updatedMap
      }) ++ additionalConfig

    val configMapUpdated = onConfigUpdating(configMap).mapValues(_.asInstanceOf[AnyRef])
    val config: Configuration = play.api.Configuration.from(configMapUpdated)


    val environment: Environment = Environment.simple(mode = applicationMode)

    val context = api.ApplicationLoader.createContext(environment, initialSettings = configMapUpdated)
    val application = ApplicationLoader(context).load(context)

    Play.start(application)

    val serverConfig: ServerConfig = ServerConfig(rootDir = new File("."), port = Some(servicePort), address = "127.0.0.1")
    val server = NettyServer.fromApplication(application, serverConfig)
    addShutdownHook(server.stop)

    server
  }


  // Give the tests a chance to access config before the application started
  def onConfigUpdating(config: Map[String, Any]): Map[String, Any] = config

  abstract override def start() {
    super.start()
    server
  }

  abstract override def stop() {
    super.stop()
    safelyStop("stopping Play server")(server.stop())
  }

  override def resource(url: String): String = s"http://localhost:$servicePort/${-/(url)}"
}

trait AdditionalConfigProvider {
  protected def additionalConfig: Map[String, _ <: Any] = Map.empty
}

trait MongoTestConfiguration extends AdditionalConfigProvider {

  self: IntegrationTestConfiguration =>

  val dbName: String = testId.toString

  abstract override protected def additionalConfig =
    super.additionalConfig + (s"$applicationMode.microservice.mongodb.uri" -> s"mongodb://localhost:27017/$dbName")
}

object UrlHelper {
  def -/(uri: String) = if (uri.startsWith("/")) uri.drop(1) else uri
}

private[it] object SafelyStop {

  def safelyStop[T](activity: String)(action: => T) {
    try {
      action
    } catch {
      case t: Throwable => Logger.error(s"An exception occurred while $activity", t)
    }
  }
}

object TestId {
  val MaxTestIdLength   = 40
  val MaxTestNameLength = MaxTestIdLength - 10
}

case class TestId(testName: String) {

  import uk.gov.hmrc.play.it.TestId._

  Option(testName) match {
    case None                                                    => throw new NullPointerException("Null test name")
    case Some("null")                                            => throw new IllegalArgumentException("'null' is not a valid test name")
    case Some("")                                                => throw new IllegalArgumentException("Empty test name")
    case Some(invalid) if invalid.length > MaxTestNameLength     => throw new IllegalArgumentException(s"Test name '$invalid' contains ${invalid.length} characters, maximum is $MaxTestNameLength")
    case Some(invalid) if !invalid.matches("^[a-zA-Z0-9\\-_]+$") => throw new IllegalArgumentException(s"Test name '$invalid' contains invalid characters, valid characters are 'a-z', 'A-Z', '0-9', '-' and '_'")
    case _                                                       => // All okay
  }

  private val format = DateTimeFormat.forPattern("HHmmssSSS").withZone(DateTimeZone.forID("Europe/London"))

  val runId = format.print(DateTimeUtils.currentTimeMillis())

  override val toString = s"$testName-$runId"
}
