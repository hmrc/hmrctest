/*
 * Copyright 2016 HM Revenue & Customs
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

import javax.inject.Inject

import uk.gov.hmrc.play.it.servicemanager.ServiceManagerClient

import scala.util.Properties._

case class ExternalService(serviceName: String, runFrom: String, classifier: Option[String] = None, version: Option[String] = None)

class ExternalServiceRunner @Inject() (serviceManagerClient: ServiceManagerClient) {
  def runFromJar(serviceName: String, classifier: Option[String] = None) = {
    val runLatestReleases = envOrElse("IT_RUN_MODE", "").equals("LATEST_RELEASES")
    val versionEnvironmentVariable = serviceManagerClient.version_variable(serviceName)
    val version = envOrElse(versionEnvironmentVariable.variable, "")

    if (!version.isEmpty)
      ExternalService(serviceName, "RELEASE_JAR", classifier, Some(version))
    else if (runLatestReleases)
      ExternalService(serviceName, "RELEASE_JAR", classifier)
    else
      ExternalService(serviceName, "SNAPSHOT_JAR", classifier, Some("999-SNAPSHOT"))
  }

  def runFromSource(serviceName: String, classifier: Option[String] = None) = ExternalService(serviceName, "SOURCE", classifier)
}
