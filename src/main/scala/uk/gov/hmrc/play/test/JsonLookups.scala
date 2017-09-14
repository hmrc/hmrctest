/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.play.test

import play.api.libs.json.{JsDefined, JsValue, Writes}

/**
  * Convenience trait for testing against against Play JSON lookups.
  * Play JSON API changes now mean that expressions of the kind:
  * <pre>
  *   authority \ "uri"
  * </pre>
  * now return a <code>JsLookupResult</code> that is either <code>JsDefined</code> or the
  * previously-used <code>JsUndefined</code>. Use the <code>definedAs</code> helper below
  * for more readable test code eg
  * <pre>
  *   authority \ "uri" shouldBe definedAs(defaultGGAuthority.uri)
  * </pre>
  * There should be an implicit <code>Writes[T]</code> for the raw value to implicitly convert it to
  * the appropriate <code>JsValue</code> which will then be wrapped in <code>JsDefined</code>
  * for comparison during assertions.
  */
trait JsonLookups {
  import scala.language.implicitConversions
  implicit def toJsFieldJsValue[T](field: T)(implicit w: Writes[T]): JsValue = w.writes(field)

  def definedAs(value: JsValue): JsDefined = JsDefined(value)
}
