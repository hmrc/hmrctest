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

import play.api.libs.json.{JsPath, JsonValidationError}

class JsException(method: String, url: String, body: String, clazz: Class[_], errors: Seq[(JsPath, Seq[JsonValidationError])]) extends Exception {
  override def getMessage: String = {
    s"$method of '$url' returned invalid json. Attempting to convert to ${clazz.getName} gave errors: $errors"
  }
}
